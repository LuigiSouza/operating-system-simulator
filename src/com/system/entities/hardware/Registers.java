package com.system.entities.hardware;

import com.system.handlers.enumState;

public class Registers {
    private int PC;
    private int Accumulator;
    private enumState State;

    public Registers(int pc,int A, enumState state) {
        this.PC = pc;
        this.Accumulator = A;
        this.State = state;
    }

    public void setState(enumState state) { this.State = state; }
    protected void setAccumulator(int Accumulator) { this.Accumulator = Accumulator; }
    protected void setPC(int PC) { this.PC = PC; }

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
