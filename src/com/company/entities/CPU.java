package com.company.entities;

import com.company.handlers.Tuple;
import com.company.handlers.enumCommands;

public class CPU extends cpuBasic {

    public CPU (int i) {
        this.PC = 0;
        this.cpuStop = false;
        this.Accumulator = 0;
        this.memory = new int[i];
    }

    public void setCpuStopToNormal() {
        cpuStop = true;
        PC++;
    }

    public int getPC() {
        return PC;
    }

    public int[] getMemory() {
        return memory;
    }

    public int getAccumulator() {
        return Accumulator;
    }

    public boolean isCpuStop() {
        return cpuStop;
    }
}
