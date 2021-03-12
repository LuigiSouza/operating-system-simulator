package com.system.entities.os;

import com.system.entities.hardware.Timer;
import com.system.entities.hardware.CPU;
import com.system.entities.memory.MMU;
import com.system.entities.hardware.PhysicalMemory;
import com.system.handlers.VarsMethods;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.io.FileWriter;
import java.io.IOException;

import static com.system.handlers.VarsMethods.initial_quantum;

public class SO {

    protected final Scheduler scheduler;

    protected CPU cpu;

    protected static Timer timer;

    private static int quantum = initial_quantum;

    private Controller self_controller;

    public int read_call = 0;
    public int write_call = 0;
    public int errors_call = 0;
    public int stop_call = 0;

    private int[][] IO;
    private int[] counter;
    private int[] cost;

    private PhysicalMemory physicalMemory;
    private MMU mmu;

    private void load_files(Process p) {
        IO = p.getIO();
        counter = p.getCounter();
        cost = p.getCost();
    }

    public SO(String str, String out) {
        timer = new Timer();

        scheduler = new Scheduler(str);

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

    protected void change_process(int interruption) {
        if (cpu.getState() == enumState.Stop && !scheduler.getCurrentProcess().ended) {
            stop_call++;
            scheduler.setJobEnd();
            cpu.setCpuState(enumState.Sleep);
            if(scheduler.getCurrentProcess().date_end < 0)
                scheduler.getCurrentProcess().date_end = timer.getTimer();
        }
        else if(interruption == enumStatus.Syscall.getStatus()){

            int arg = cpu.getInstruction(cpu.getPC()-1).getY();
            cpu.setCpuState(enumState.Sleep);

            if(cpu.getInstruction(cpu.getPC()-1).getX() == enumCommands.LE.getCommand())
                LE(arg);
            else if (cpu.getInstruction(cpu.getPC()-1).getX() == enumCommands.GRAVA.getCommand())
                GRAVA(arg);
            else {
                errors_call++;
                cpu.setCpuState(enumState.InvalidInstructions);
                return;
            }
            if(cpu.getState() == enumState.InvalidMemory)
                return;
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
                System.out.println("perdseu");

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
        this.cpu = new CPU(scheduler.getCurrentProcess());
        if (cpu.getState() != enumState.InvalidInstructions && cpu.getState() != enumState.InvalidMemory)
            cpu.setCpuState(enumState.Normal);
        load_files(scheduler.getCurrentProcess());

        System.out.printf("\nCPU: %d: \n", scheduler.getProcessControl());
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