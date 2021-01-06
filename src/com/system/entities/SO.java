package com.system.entities;

import com.system.entities.CPU;
import com.system.handlers.enumState;

import java.util.ArrayList;

public class SO {


    private CPU cpu;
    public static Timer timer;

    public SO(int i, int j) {
        cpu = new CPU(i, j);

        timer = new Timer();
    }

    public void start() {

        enumState ret = enumState.Normal;
        while(ret != enumState.Stop) {
            ret = cpu.executeAll();

            if(ret == enumState.Save) {
                timer.setInterruption(timer.getTimer()+1, "salvamento");
                cpu.setCpuStopToNormal();
            }
            if(ret == enumState.Read) {
                timer.setInterruption(timer.getTimer()+1, "leitura");
                cpu.setCpuStopToNormal();
            }

            timer.updateTimer();
        }

    }

    public CPU getCpu() {
        return cpu;
    }
}
