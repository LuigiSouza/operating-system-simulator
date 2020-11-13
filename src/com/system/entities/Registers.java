package com.system.entities;

import com.system.handlers.enumState;

public class Registers {
    private int PC;
    private int Accumulator;
    private int State;

    public Registers(int pc,int A, int state) {
        this.PC = pc;
        this.Accumulator = A;
        this.State = state;
    }

    public int getState() {
        return State;
    }

    public int getAccumulator() {
        return Accumulator;
    }

    public int getPC() {
        return PC;
    }

    public void setState(int state) {
        State = state;
    }

    public void setAccumulator(int accumulator) {
        Accumulator = accumulator;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }
}
