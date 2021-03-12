package com.system.entities.os;

import com.system.handlers.VarsMethods;
import com.system.handlers.enumStatus;


public class Controller {

    private final SO so;

    public Controller(SO so) {

        this.so = so;

    }

    public void run(){

        while (!so.scheduler.isEnd() && !so.error() && SO.timer.getTimer() < 500) {

            //System.out.println("laco " + so.scheduler.isEnd() + so.error());
            int pause = so.cpu.execute();

            SO.timer.updateTimer();

            if (pause == enumStatus.Next.getStatus() || pause == enumStatus.Syscall.getStatus()){
                so.scheduler.getCurrentProcess().time_cpu++;
                so.scheduler.time_cpu++;
                SO.subQuantum();
            }
            if (pause != enumStatus.Next.getStatus())
                so.change_process(pause);

            int update = SO.timer.dealInterruption();
            while (update != -1) {
                if (update == VarsMethods.periodic_pause)
                    so.deal_periodic();
                else if (update > -1) {
                    so.scheduler.setProcessNormal(update);
                    so.scheduler.unlockProcess(update);
                }
                update = SO.timer.dealInterruption();
            }
        }

    }

}
