package com.system.entities;

import com.system.handlers.enumState;
import com.system.handlers.enumStatus;


public class Controller {

    private final SO so;

    public Controller(SO so) {

        this.so = so;

    }

    public void run(){
        so.scheduler.time_cpu_begin = System.nanoTime();
        so.scheduler.getCurrentProcess().time_cpu_begin = System.nanoTime();

        while (!so.scheduler.isEnd() && !so.error() && SO.timer.getTimer() < 200) {

            System.out.print("laco ");

            if (so.cpu.getState() != enumState.Normal)
                so.scheduler.time_idle_begin = System.nanoTime();

            int pause = so.cpu.execute();

            int update = SO.timer.updateTimer();

            if (pause == enumStatus.Next.getStatus() || pause == enumStatus.Syscall.getStatus()){
                so.scheduler.getCurrentProcess().time_cpu++;
                so.scheduler.time_cpu++;
                SO.subQuantum();
            }
            if (pause != enumStatus.Next.getStatus())
                so.change_process(pause);

            if (update == -2)
                so.deal_periodic();

            if (so.cpu.getState() == enumState.Normal)
                so.scheduler.update_idle_time();

            if (so.cpu.getState() != enumState.Normal && so.scheduler.time_idle_begin < 0)
                so.scheduler.time_idle_begin = System.nanoTime();

            if (update > -1) {
                so.scheduler.setProcessNormal(update);
                so.scheduler.unlockProcess(update);
                if(so.scheduler.isCurrent(update))
                    so.scheduler.getCurrentProcess().time_cpu_begin = System.nanoTime();
            }
        }
        so.scheduler.update_idle_time();
        so.scheduler.total_time_cpu += System.nanoTime() - so.scheduler.time_cpu_begin;
    }

}
