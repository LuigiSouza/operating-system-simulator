package com.system.handlers;

public enum enumCommands {
    CARGI(0),
    CARGM(1),
    CARGX(2),
    ARMM(3),
    ARMX(4),
    SOMA(5),
    NEG(6),
    DESVZ(7),
    PARA(8),
    LE(9),
    GRAVA(10),
    ERROR(11);

    private final int command;

    enumCommands(int i) {
        this.command = i;
    }

    public int getCommand() {
        return command;
    }
}
