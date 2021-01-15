package com.system.entities;

import com.system.handlers.enumState;

public class Controller {

    public static Timer timer;

    private static CPU cpu;

    private static SO so;

    public Controller() {

        timer = new Timer();

        so = new SO("jobs.json");

        System.out.println("CPU 0:");
        cpu = new CPU(so.escalonador.getCurrentProcess());
        System.out.println(cpu.getState());
        timer.setPeriodic();

    }

    private void change_process(int interruption, int preempcao) {
        if (cpu.getState() == enumState.Stop)
            so.escalonador.setJobEnd();
        else
            timer.setInterruption(preempcao != -2 ? interruption : 5, so.escalonador.getProcessControl());

        so.escalonador.block();

        cpu.setCpuStop(enumState.Sleep);

        if (so.escalonador.nextJob() > -1) {
            cpu = new CPU(so.escalonador.getCurrentProcess());
            System.out.println("CPU " + so.escalonador.getProcessControl() + ":");
            timer.setPeriodic();
        }
    }

    public void run(){
        while (!so.escalonador.isEnd() && !so.error()) {

            int pause = cpu.execute();

            int update = timer.updateTimer();

            if (pause > -1 || update == -2 || cpu.getState() == enumState.Stop || cpu.getState() == enumState.Sleep)
                change_process(pause, update);

            if (update > -1) {
                so.escalonador.setProcessNormal(update);
                so.escalonador.unlockProcess(update);
            }
        }
    }
}
