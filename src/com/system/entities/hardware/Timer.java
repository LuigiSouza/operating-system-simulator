package com.system.entities.hardware;

import com.system.handlers.MinHeap;
import com.system.handlers.VarsMethods;
import com.system.entities.os.Process;

public class Timer {

    public int initial_quantum;

    private int timer;

    // timer min heap
    private static final int max_pauses = 10;

    private final MinHeap interruption;

    public Timer(int initial_quantum){
        this.initial_quantum = initial_quantum;
        timer = 0;
        interruption = new MinHeap(max_pauses);
    }

    public int dealInterruption() {
        if(this.interruption.CheckMin().getX() != timer) return -1;

        //System.out.println("Timer: '" + this.interruption.get(i) + "' interruption");
        return this.interruption.extractMin().getY();
    }

    public int getTimer() {
        return timer;
    }

    public void updateTimer() {
        this.timer++;
    }

    public void setInterruption(int i, int pause, Process job) {
        job.addInterruption();
        this.interruption.insert(this.timer + i, pause);
    }

    public void setPeriodic() {
        this.interruption.remove_periodic();
        this.interruption.insert(this.timer + initial_quantum, VarsMethods.periodic_pause);
    }
}
