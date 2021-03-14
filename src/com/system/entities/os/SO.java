package com.system.entities.os;

import com.system.entities.hardware.Timer;
import com.system.entities.hardware.CPU;
import com.system.entities.memory.MMU;
import com.system.entities.hardware.PhysicalMemory;
import com.system.entities.memory.PageDescriber;
import com.system.entities.memory.PagesTable;
import com.system.handlers.VarsMethods;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static com.system.handlers.VarsMethods.initial_quantum;

public class SO {

    public static final int SIZE_MEM = 60;
    public static final int SIZE_PAGE = 2;
    public static final int NUM_PAGE = 30;

    protected final Scheduler scheduler;

    protected CPU cpu;

    protected static Timer timer;

    private static int quantum = initial_quantum;

    private final Controller self_controller;

    public int read_call = 0;
    public int write_call = 0;
    public int errors_call = 0;
    public int stop_call = 0;

    private int[][] IO;
    private int[] counter;
    private int[] cost;

    // fst: stores job; snd: stores page
    private PageDescriber[] mapPhysicalMemory;

    public PhysicalMemory physicalMemory;
    private MMU mmu;

    private LinkedList<PageDescriber> FIFO_Controller2;
    private int FIFO_Controller = 0;

    private int[][] secondaryMemory;

    private void load_files(Process p) {
        IO = p.getIO();
        counter = p.getCounter();
        cost = p.getCost();
    }

    public SO(String str, String out) {
        timer = new Timer();

        scheduler = new Scheduler(str);

        physicalMemory = new PhysicalMemory(SIZE_MEM, SIZE_PAGE);

        //mapPhysicalMemory = new int[physicalMemory.getSize_memory()][2];
        mapPhysicalMemory = new PageDescriber[physicalMemory.getSize_memory()];
        secondaryMemory = new int[scheduler.getJobSize()][NUM_PAGE*SIZE_PAGE];

        FIFO_Controller2 = new LinkedList<>();

        mmu = new MMU(physicalMemory);

        cpu = new CPU(mmu);

        load_new_cpu();

        load_files(scheduler.getCurrentProcess());

        timer.setPeriodic();

        self_controller = new Controller(this);

        self_controller.run();

        end(out);

    }

    private void end(String str) {
        if(!error())
            print_bench();
        else
            print_error();
        SaveFile(str);
    }

    private void copy_fst_snd(int bgn, int end, int[] fst, int[] snd) {
        for (int i = 0; bgn < end; bgn++, i++)
            snd[bgn] = fst[i];
    }

    private void deal_FIFO(int index) {
        System.out.println("Tudo Cheio");
        int tmp = FIFO_Controller;
        while(!mapPhysicalMemory[FIFO_Controller].is_changeable()) {

            FIFO_Controller = ++FIFO_Controller >= mapPhysicalMemory.length ? 0 : FIFO_Controller;

            if (FIFO_Controller == tmp) {
                System.out.println("Perdemo");
            }
        }

        if (mapPhysicalMemory[FIFO_Controller].was_changed()) {
            // esvazia memoria principal
            int id = mapPhysicalMemory[FIFO_Controller].getId();
            copy_fst_snd(id, id+SIZE_PAGE, physicalMemory.read_page(FIFO_Controller), secondaryMemory[FIFO_Controller]);
            mapPhysicalMemory[FIFO_Controller].setChanged(false);
            mapPhysicalMemory[FIFO_Controller].setValid(false);
            mapPhysicalMemory[FIFO_Controller].setChangeable(true);
            mapPhysicalMemory[FIFO_Controller].setAccessed(false);
            mapPhysicalMemory[FIFO_Controller].setFrame(-1);
        }

    }

    private void deal_FIFO_withChance() {

    }

    protected void change_process(int interruption) {
        // Deal Stop Instruction
        if (cpu.getState() == enumState.Stop && !scheduler.getCurrentProcess().ended) {
            System.out.println("Stop");
            stop_call++;
            scheduler.setJobEnd();
            cpu.setCpuState(enumState.Sleep);
            if(scheduler.getCurrentProcess().date_end < 0)
                scheduler.getCurrentProcess().date_end = timer.getTimer();
        }
        // Deal Page Fault
        else if (cpu.getState() == enumState.PageFault) {
            int primary_memory;
            for(primary_memory = 0; primary_memory < mapPhysicalMemory.length; primary_memory++ ) {
                if(mapPhysicalMemory[primary_memory] == null)
                    break;
            }
            if(primary_memory == mapPhysicalMemory.length) {
                deal_FIFO(primary_memory);
                primary_memory = FIFO_Controller;
            }

            int arg = cpu.getInstruction(cpu.getPC()).getY();
            mapPhysicalMemory[primary_memory] = mmu.getPage(arg);
            mapPhysicalMemory[primary_memory].setValid(true);
            mapPhysicalMemory[primary_memory].setChangeable(false);
            mapPhysicalMemory[primary_memory].setFrame(primary_memory);
            int index = (arg/SIZE_PAGE)*SIZE_PAGE;
            // preenche memória principal
            physicalMemory.write_page(Arrays.copyOfRange(secondaryMemory[scheduler.getProcessControl()], index, index+SIZE_PAGE), primary_memory);

            cpu.setCpuState(enumState.Sleep);
            timer.setInterruption(2, scheduler.getProcessControl());

        }
        // Deal Syscall
        else if(interruption == enumStatus.Syscall.getStatus()){
            System.out.println("Syscall");

            int arg = cpu.getInstruction(cpu.getPC()-1).getY();
            cpu.setCpuState(enumState.Sleep);

            if(cpu.getInstruction(cpu.getPC()-1).getX() == enumCommands.LE.getCommand())
                LE(arg);
            else if (cpu.getInstruction(cpu.getPC()-1).getX() == enumCommands.GRAVA.getCommand())
                GRAVA(arg);
            else {
                errors_call++;
                cpu.setCpuState(enumState.InvalidInstructions);
                System.out.println("Instrução Invalida");
                return;
            }
            if(cpu.getState() == enumState.InvalidMemory) {
                System.out.println("Leitura de Memoria Invalida");
                return;
            }
            timer.setInterruption(cost[arg], scheduler.getProcessControl());
            scheduler.getCurrentProcess().time_blocked += cost[arg];
        }

        if(!scheduler.getCurrentProcess().blocked)
            scheduler.getCurrentProcess().blocked_times++;

        scheduler.block();
        resetQuantum();

        if (scheduler.nextJob() > -1){
            load_new_cpu();

            scheduler.getCurrentProcess().times_schedule++;

            timer.setPeriodic();
        }
    }

    public void deal_periodic() {
        if (quantum == 0) {
            enumState temp = cpu.getState();
            cpu.setCpuState(enumState.Sleep);

            if (scheduler.nextJob() > -1) {
                scheduler.getCurrentProcess().times_lost++;
                System.out.println("-- Perdeu CPU --");

                load_new_cpu();

                scheduler.getCurrentProcess().times_schedule++;

            }
            else
                cpu.setCpuState(temp);

            resetQuantum();
        }
        timer.setPeriodic();
    }

    private void LE(int n) {
        read_call++;
        if (n < IO.length && counter[n] < IO[n].length) {
            cpu.setAccumulator(IO[n][counter[n]]);
            counter[n]++;
        }
        else {
            cpu.setCpuState(enumState.InvalidMemory);
            System.out.println("Read File Overflow!");
        }
    }

    private void GRAVA(int n) {
        write_call++;
        if (n < IO.length && counter[n] < IO[n].length) {
            IO[n][counter[n]] = cpu.getAccumulator();
            counter[n]++;
        }
        else {
            cpu.setCpuState(enumState.InvalidMemory);
            System.out.println("Write File Overflow!");
        }
    }

    public void resetQuantum() {
        quantum = initial_quantum;
    }

    public static void subQuantum() {
        quantum = Math.max(0, Math.min(quantum - 1, quantum));
    }

    public static int getQuantum() {
        return quantum;
    }


    public boolean error() {
        return (scheduler.getCurrentProcess().getState() == enumState.InvalidMemory ||
                scheduler.getCurrentProcess().getState() == enumState.InvalidInstructions);
    }

    private void load_new_cpu() {
        scheduler.loadNext();
        this.cpu.loadJob(scheduler.getCurrentProcess());
        if (cpu.getState() != enumState.InvalidInstructions && cpu.getState() != enumState.InvalidMemory)
            cpu.setCpuState(enumState.Normal);
        load_files(scheduler.getCurrentProcess());

        mmu.changePagesTable(scheduler.getCurrentProcess().getPagesTable());
        mmu.setPagesTableChangeable();

        System.out.printf("\nCarregou CPU: %d: \n", scheduler.getProcessControl());
    }

    // Save instructions into a file
    public void SaveFile(String str) {
        try {
            FileWriter myWriter = new FileWriter(str);

            myWriter.write(VarsMethods.output);

            myWriter.close();

            System.out.println("File " + str + " created.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e);
        }
    }

    public void print_error() {
        // Para facilitar leirtura
        int pc = scheduler.getCurrentProcess().getRegisters().getPC();
        int job = scheduler.getProcessControl();
        enumState st = scheduler.getCurrentProcess().getState();
        Object arg = cpu.getInstruction(pc-1).getY();

        VarsMethods.output += "Error no Processo " + job + ": " + st + ", Instrucao " + pc + ": " + enumCommands.values()[cpu.getInstruction(pc-1).getX()] + " " + (arg == null ? "" : arg) + "\n";
    }

    public void print_bench() {
        long os_time = System.nanoTime() - VarsMethods.start;
        VarsMethods.output += "\nOS Total time: " + (os_time / 1000000d)  + "ms\n";
        scheduler.printResults();
        printResults();
    }

    public void printResults() {
        VarsMethods.output += "\nResultados Gerais:\n";
        VarsMethods.output += "Timer: " + timer.getTimer() + "\n";
        VarsMethods.output += "Tempo Ativo: " + scheduler.time_cpu + "\n";
        VarsMethods.output += "Tempo Ocioso: " + (timer.getTimer()-scheduler.time_cpu) + "\n";
        VarsMethods.output += "Total de Chamadas do SO: " + (this.write_call+this.read_call+this.errors_call+this.stop_call) + "\n";
        VarsMethods.output += "Chamada do tipo LE: " + this.read_call + "\n";
        VarsMethods.output += "Chamada do tipo GRAVA: " + this.write_call + "\n";
        VarsMethods.output += "Chamada do tipo STOP: " + this.stop_call + "\n";
        VarsMethods.output += "Chamada do tipo Ilegal: " + this.errors_call + "\n";
        VarsMethods.output += "Trocas de Processo: " + scheduler.changes + "\n";
        VarsMethods.output += "Numero de Preempsoes: " + scheduler.preemption_times + "\n";
    }
}
