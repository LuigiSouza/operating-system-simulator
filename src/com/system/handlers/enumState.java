package com.system.handlers;

public enum enumState {
    Normal(0),
    InvalidInstructions(1),
    InvalidMemory(2),
    Stop(3),
    Read(4),
    Save(5);

    private int state;

    enumState(int state) {
        this.state = state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
