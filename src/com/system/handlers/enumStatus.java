package com.system.handlers;

public enum enumStatus {
    Next(1),
    Syscall(2),
    Stop(3),
    Error(4);

    private final int Status;

    enumStatus(int i) {
        this.Status = i;
    }

    public int getStatus() {
        return Status;
    }
}
