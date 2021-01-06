package com.system.entities;

import com.system.handlers.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Timer {

    private int timer;
    private float dt;

    private Map<Integer, String> interruption = new HashMap<>();


    public Timer(){
        timer = 0;
        dt = 0f;
    }

    public void dealInterruption(int i) {
        if(this.interruption.get(i) == null) return;

        System.out.println("Dealing with '" + this.interruption.get(i) + "' interruption");
        try { Thread.sleep (1000); } catch (InterruptedException ex) { System.out.println(ex); }
        this.interruption.remove(i);
    }

    public void addTimer() {
        this.timer++;
    }

    public int getTimer() {
        return timer;
    }

    public void updateTimer() {
        this.timer++;
        dealInterruption(timer);
    }


    public void setInterruption(int i, String pause) {
        this.interruption.put(i, pause);
    }
}
