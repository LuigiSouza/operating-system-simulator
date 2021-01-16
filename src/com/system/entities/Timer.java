package com.system.entities;

import com.system.handlers.VarsMethods;

import java.util.HashMap;
import java.util.Map;

import static com.system.handlers.VarsMethods.initial_quantum;

public class Timer {

    private int timer;

    private int periodic_pause = 0;

    private final Map<Integer, Integer> interruption;


    public Timer(){
        timer = 0;
        interruption = new HashMap<>();
    }

    public int dealInterruption(int i) {
        if(this.interruption.get(i) == null) return -1;

        int ret = this.interruption.get(i);
        //System.out.println("Timer: '" + this.interruption.get(i) + "' interruption");
        this.interruption.remove(i);
        return ret;
    }

    public int getTimer() {
        return timer;
    }

    public int updateTimer() {
        this.timer++;
        return dealInterruption(timer);
    }


    public void setInterruption(int i, int pause) {
        while (this.interruption.get(this.timer + i) != null) {
            i++;
        }
        this.interruption.put(this.timer + i, pause);
    }

    public void setPeriodic() {
        this.interruption.remove(periodic_pause);
        this.periodic_pause = this.timer + initial_quantum;
        while (this.interruption.get(this.periodic_pause) != null) {
            periodic_pause++;
        }
        this.interruption.put(this.periodic_pause, VarsMethods.periodic_pause);
    }
}
