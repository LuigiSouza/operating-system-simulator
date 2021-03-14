package com.system.entities.os;

import com.system.entities.hardware.Timer;
import com.system.entities.hardware.CPU;
import com.system.entities.memory.MMU;
import com.system.entities.hardware.PhysicalMemory;
import com.system.entities.memory.PageDescriber;
import com.system.handlers.VarsMethods;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.system.handlers.VarsMethods.initial_quantum;

public class SO {

    public static final int SIZE_MEM = 60;
    public static final int SIZE_PAGE = 2;
    public static final int NUM_PAGE = 30;
    public static final int INTERRUPTION_WRITE = 2;
    public static final int INTERRUPTION_CLEAN = 2;

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
    private final PageDescriber[] mapPhysicalMemory;

    public PhysicalMemory physicalMemory;

    private final MMU mmu;

    private final ArrayList<Integer> FIFO_Controller;

    private final int[][] secondaryMemory;


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

        FIFO_Controller = new ArrayList<>();
        for(int i = 0; i < mapPhysicalMemory.length; i++)
            FIFO_Controller.add(i);

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

    private int deal_FIFO() {
        int index = -1;

        for(int i = 0; i < FIFO_Controller.size(); i++)
            if(mapPhysicalMemory[FIFO_Controller.get(i)] == null || mapPhysicalMemory[FIFO_Controller.get(i)].is_changeable()) {
                index = FIFO_Controller.get(i);
                FIFO_Controller.remove(i);
                FIFO_Controller.add(0, index);
                break;
            }

        if(index == -1) {
            System.out.println("Perdemo");
            return -1;
        }

        if(mapPhysicalMemory[index] == null)
            return index;

        if (mapPhysicalMemory[index].was_changed()) {
            // esvazia memoria principal
            int id = mapPhysicalMemory[index].getId();
            copy_fst_snd(id, id+SIZE_PAGE, physicalMemory.read_page(index), secondaryMemory[index]);
            mapPhysicalMemory[index].setChanged(false);

            timer.setInterruption(INTERRUPTION_WRITE+INTERRUPTION_CLEAN, scheduler.getProcessControl(), scheduler.getCurrentProcess());
        }

        mapPhysicalMemory[index].setAccessed(false);
        mapPhysicalMemory[index].setChangeable(true);
        mapPhysicalMemory[index].setValid(false);
        mapPhysicalMemory[index].setFrame(-1);

        return index;
    }

    private void deal_FIFO_withChance() {

    }

    protected void change_process(int interruption) {
        // Deal Stop Instruction
        if (cpu.getState() == enumState.Stop && !scheduler.getCurrentProcess().ended) {
            System.out.println("Stop");
            deal_stop();
        }
        else if (cpu.getState() == enumState.PageFault) {
            deal_page_fault();
        }
        else if(interruption == enumStatus.Syscall.getStatus()){
            System.out.println("Syscall");
            deal_syscall();
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

    private void deal_stop() {
        stop_call++;
        scheduler.setJobEnd();
        cpu.setCpuState(enumState.Sleep);
        if(scheduler.getCurrentProcess().date_end < 0)
            scheduler.getCurrentProcess().date_end = timer.getTimer();
        free_map_primary(scheduler.getCurrentProcess());
    }

    private void free_map_primary(Process job) {
        for(PageDescriber page : job.getPagesTable().getPageDescribers()) {
            int frame = page.getFrame();

            if (frame == -1)
                continue;

            FIFO_Controller.remove((Integer) frame);
            FIFO_Controller.add(0, frame);
            mapPhysicalMemory[frame] = null;
        }
    }

    private void deal_syscall() {

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
        timer.setInterruption(cost[arg], scheduler.getProcessControl(), scheduler.getCurrentProcess());
        scheduler.getCurrentProcess().time_blocked += cost[arg];
    }

    private void deal_page_fault() {

        int primary_memory = deal_FIFO();

        int arg = cpu.getInstruction(cpu.getPC()).getY();

        mapPhysicalMemory[primary_memory] = mmu.getPage(arg);
        mapPhysicalMemory[primary_memory].setValid(true);
        mapPhysicalMemory[primary_memory].setChangeable(false);
        mapPhysicalMemory[primary_memory].setFrame(primary_memory);

        int index = (arg/SIZE_PAGE)*SIZE_PAGE;
        // preenche memória principal
        physicalMemory.write_page(Arrays.copyOfRange(secondaryMemory[scheduler.getProcessControl()], index, index+SIZE_PAGE), primary_memory);

        cpu.setCpuState(enumState.Sleep);
        timer.setInterruption(INTERRUPTION_WRITE, scheduler.getProcessControl(), scheduler.getCurrentProcess());

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


    public void printOut() {
        this.scheduler.print_Jobs_IO();
    }

}
