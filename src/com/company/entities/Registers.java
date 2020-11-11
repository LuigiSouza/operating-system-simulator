package com.company.entities;

import com.company.handlers.enumState;

public class Registers {
    private int PC;
    private int Accumulator;
    private enumState State;

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

    public void setState(enumState state) {
        State = state;
    }

    public void setAccumulator(int accumulator) {
        Accumulator = accumulator;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }
}
