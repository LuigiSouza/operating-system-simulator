package com.system.entities;

import com.system.handlers.enumState;

public class Registers {
    protected int PC;
    protected int Accumulator;
    protected enumState State;

    public Registers(int pc,int A, enumState state) {
        this.PC = pc;
        this.Accumulator = A;
        this.State = state;
    }

    public enumState getState() {
        return State;
    }

    public int getAccumulator() {
        return Accumulator;
    }

    public int getPC() {
        return PC;
    }

}
