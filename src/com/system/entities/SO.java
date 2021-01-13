package com.system.entities;

import com.system.handlers.enumState;

public class SO {


    private CPU cpu;

    public static Timer timer;

    private Escalonador escalonador;

    public SO() {
        timer = new Timer();

        escalonador = new Escalonador();

        escalonador.readJobs("jobs.json");

        cpu = new CPU(escalonador.getCurrentProcess());
    }

    public void start() {

        while (!escalonador.isEnd()) {

            int pause = cpu.execute();
            int update = timer.updateTimer();

            if (pause > -1 || cpu.getState() == enumState.Stop) {
                if(cpu.getState() == enumState.Stop)
                    escalonador.setJobEnd();
                else
                    timer.setInterruption(pause, escalonador.getProcessControl());

                escalonador.block();

                if(escalonador.nextJob() > -1)
                    cpu = new CPU(escalonador.getCurrentProcess());

            }
            if (update > -1 ) {
                escalonador.setProcessNormal(update);
                escalonador.unlockProcess(update);
            }
        }

    }

}
