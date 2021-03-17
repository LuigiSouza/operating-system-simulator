package com.system.entities.os;

import static com.system.handlers.VarsMethods.periodic_pause;
import com.system.handlers.enumStatus;


public class Controller {

    public static final int TIMER_TOTAL = 5000;

    private final SO so;

    public Controller(SO so) {

        this.so = so;

    }

    public void run(){

        while (!so.scheduler.isEnd() && !so.error() && SO.timer.getTimer() < TIMER_TOTAL) {

            int pause = so.cpu.execute();

            SO.timer.updateTimer();

            if (pause == enumStatus.Next.getStatus() || pause == enumStatus.Syscall.getStatus()){
                //so.printMemory();
                //so.printTimer();
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
        so.printIO();
        so.printSecMemory();

        if(so.scheduler.isEnd())
            System.out.println("--- Successfully Finished");
        else if(so.error())
            System.out.println("--- Error Caught");
        else if((SO.timer.getTimer() >= TIMER_TOTAL))
            System.out.println("--- Time is Up");

    }

}
