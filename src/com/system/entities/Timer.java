package com.system.entities;

import com.system.handlers.MinHeap;
import com.system.handlers.VarsMethods;

import static com.system.handlers.VarsMethods.initial_quantum;

public class Timer {

    private int timer;

    private int periodic_pause = 0;

    private final MinHeap interruption;


    public Timer(){
        timer = 0;
        interruption = new MinHeap(VarsMethods.max_pauses);
    }

    public int dealInterruption() {
        if(this.interruption.CheckMin().getX() != timer) return -1;

        int ret = this.interruption.extractMin().getY();
        //System.out.println("Timer: '" + this.interruption.get(i) + "' interruption");
        return ret;
    }

    public int getTimer() {
        return timer;
    }

    public void updateTimer() {
        this.timer++;
    }


    public void setInterruption(int i, int pause) {
        this.interruption.insert(this.timer + i, pause);
    }

    public void setPeriodic() {
        this.interruption.remove_periodic();
        this.periodic_pause = this.timer + initial_quantum;
        this.interruption.insert(this.periodic_pause, VarsMethods.periodic_pause);
    }
}
