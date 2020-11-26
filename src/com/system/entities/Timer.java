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

    private static float timePassed = 1 / 60f;

    public Timer(){
        timer = 0;
        dt = 0f;
    }

    public void addTimer() {
        this.timer++;
    }

    public int getTimer() {
        return timer;
    }

    public void updateTimer() {
        dt += timePassed;
    }

    public float getDt() {
        return dt;
    }

    public String isInterruption(int i) {
        return this.interruption.get(i);
    }

    public void setInterruption(int i, String pause) {
        this.interruption.put(i, pause);
    }
}
