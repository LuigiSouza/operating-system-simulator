package com.system.entities.hardware;

import com.system.handlers.enumState;

public class Registers {
    private int PC;
    private int Accumulator;
    private enumState State;

    public Registers() {
        this.PC = 0;
        this.Accumulator = 0;
        this.State = enumState.Normal;
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
