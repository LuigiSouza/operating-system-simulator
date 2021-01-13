package com.system.entities;

import com.system.handlers.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Timer {

    private int timer;

    private final Map<Integer, Integer> interruption;


    public Timer(){
        timer = 0;
        interruption = new HashMap<>();
    }

    public int dealInterruption(int i) {
        if(this.interruption.get(i) == null) return -1;

        int ret = this.interruption.get(i);
        System.out.println("Process '" + this.interruption.get(i) + "' is Ready");
        this.interruption.remove(i);
        return ret;
    }

    public void addTimer() {
        this.timer++;
    }

    public int getTimer() {
        return timer;
    }

    public int updateTimer() {
        this.timer++;
        return dealInterruption(timer);
    }


    public void setInterruption(int i, int pause) {
        this.interruption.put(this.timer + i, pause);
    }
}
