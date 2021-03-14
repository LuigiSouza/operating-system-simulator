package com.system.entities.os;

import static com.system.handlers.VarsMethods.periodic_pause;
import com.system.handlers.enumStatus;


public class Controller {

    public static final int TIMER_TOTAL = 2000;

    private final SO so;

    public Controller(SO so) {

        this.so = so;

    }

    public void run(){

        while (!so.scheduler.isEnd() && !so.error() && SO.timer.getTimer() < TIMER_TOTAL) {

            //System.out.println("laco " + so.scheduler.isEnd() + so.error());
            int pause = so.cpu.execute();

            SO.timer.updateTimer();

            if (pause == enumStatus.Next.getStatus() || pause == enumStatus.Syscall.getStatus()){
                //so.printMemory();
                //if(so.scheduler.getProcessControl() == 0) so.printMemory(so.scheduler.getCurrentProcess());
                so.scheduler.getCurrentProcess().time_cpu++;
                so.scheduler.time_cpu++;
                SO.subQuantum();
            }
            if (pause != enumStatus.Next.getStatus())
                so.change_process(pause);

            int update = SO.timer.dealInterruption();
            while (update != -1) {
                if (update == periodic_pause)
                    so.deal_periodic();
                else if (update > -1) {
                    so.scheduler.deal_interruption(update);
                }
                update = SO.timer.dealInterruption();
            }

        }
        so.printOut();
        System.out.println(!so.scheduler.isEnd() + " " +  !so.error()  + " " + (SO.timer.getTimer() < TIMER_TOTAL));

    }

}
