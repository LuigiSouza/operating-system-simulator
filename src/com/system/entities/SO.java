package com.system.entities;

import com.system.entities.CPU;
import com.system.handlers.enumState;

import java.util.ArrayList;

public class SO {

    private int[][] IO;
    private final int[] counter = { 0, 0 };

    private CPU cpu;
    public static Timer timer;

    public SO(int i) {
        int sizeMem = i;

        cpu = new CPU(sizeMem);
        IO = new int[2][sizeMem];

        timer = new Timer();
    }

    public void start() {

        enumState ret = enumState.Normal;
        while(ret != enumState.Stop) {
            ret = cpu.executeAll();

            if(ret == enumState.Save) {
                IO[1][counter[1]] = cpu.getAccumulator();
                cpu.setCpuStopToNormal();
            }
            if(ret == enumState.Read) {
                cpu.setAccumulator(IO[0][counter[0]]);
                cpu.setCpuStopToNormal();
            }

        }

    }

    public CPU getCpu() {
        return cpu;
    }
}
