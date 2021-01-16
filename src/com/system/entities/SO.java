package com.system.entities;

import com.system.handlers.VarsMethods;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.util.concurrent.TimeUnit;

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

    private void load_files(Process p) {
        IO = p.getIO();
        counter = p.getCounter();
        cost = p.getCost();
    }

    public SO(String str) {
        timer = new Timer();

        scheduler = new Scheduler(str);

        load_new_cpu();

        load_files(scheduler.getCurrentProcess());

        timer.setPeriodic();

        self_controller = new Controller(this);

        //System.out.printf("CPU: %d: \n", scheduler.getProcessControl());
        scheduler.getCurrentProcess().time_cpu_begin = System.nanoTime();

        self_controller.run();
    }

    protected void change_process(int interruption) {
        if (cpu.getState() == enumState.Stop) {
            stop_call++;
            scheduler.setJobEnd();
            if(scheduler.getCurrentProcess().date_end < 0) {
                scheduler.getCurrentProcess().date_end = timer.getTimer();
                scheduler.getCurrentProcess().time_end = System.nanoTime();
            }
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
            timer.setInterruption(cost[arg], scheduler.getProcessControl());
            scheduler.getCurrentProcess().time_blocked += cost[arg];
        }
        scheduler.getCurrentProcess().update_cpu_time();

        if(!scheduler.getCurrentProcess().blocked){
            scheduler.getCurrentProcess().time_blocked_begin = System.nanoTime();
            scheduler.getCurrentProcess().blocked_times++;
        }
        scheduler.block();
        resetQuantum();

        if (scheduler.nextJob() > -1){
            load_new_cpu();
            scheduler.getCurrentProcess().time_cpu_begin = System.nanoTime();
            scheduler.getCurrentProcess().times_schedule++;

            //System.out.printf("\nCPU: %d: \n", scheduler.getProcessControl());

            timer.setPeriodic();
        }
    }

    public void deal_periodic() {
        if (quantum == 0) {
            enumState temp = cpu.getState();
            cpu.setCpuState(enumState.Sleep);

            if (scheduler.nextJob() > -1) {
                scheduler.getCurrentProcess().times_lost++;
                scheduler.getCurrentProcess().update_cpu_time();
                load_new_cpu();
                scheduler.getCurrentProcess().time_cpu_begin = System.nanoTime();
                scheduler.getCurrentProcess().times_schedule++;

                //System.out.printf("\nCPU: %d: \n", scheduler.getProcessControl());
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
        else
            System.out.println("Read File Overflow!");
    }

    private void GRAVA(int n) {
        write_call++;
        if (n < IO.length && counter[n] < IO[n].length) {
            IO[n][counter[n]] = cpu.getAccumulator();
            counter[n]++;
        }
        else
            System.out.println("Write File Overflow!");
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
        this.cpu = new CPU(scheduler.getCurrentProcess());
        if (cpu.getState() != enumState.InvalidInstructions && cpu.getState() != enumState.InvalidMemory)
            cpu.setCpuState(enumState.Normal);
        load_files(scheduler.getCurrentProcess());
    }

    public void printResults() {
        System.out.println("\nResultados Gerais:");
        System.out.println("Timer: " + timer.getTimer());
        System.out.println("Tempo Ativo: " + scheduler.time_cpu + " ás " + ((scheduler.total_time_cpu-scheduler.time_idle_begin)/ 1000000d) + "ms");
        System.out.println("Tempo Ocioso: " + (timer.getTimer()-scheduler.time_cpu) + " ás " + ((scheduler.total_time_idle - VarsMethods.start) / 1000000d) + "ms");
        System.out.println("Total de execuçoes do SO: " + (this.write_call+this.read_call+this.errors_call+this.stop_call));
        System.out.println("Execucao do tipo LE: " + this.read_call);
        System.out.println("Execucao do tipo GRAVA: " + this.write_call);
        System.out.println("Execucao do tipo STOP: " + this.stop_call);
        System.out.println("Execucao do tipo Ilegal: " + this.errors_call);
        System.out.println("Trocas de Processo: " + scheduler.changes);
        System.out.println("Numero de Preempsoes: " + scheduler.preemption_times);
    }
}
