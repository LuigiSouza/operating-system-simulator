package com.system.entities;

import com.system.handlers.enumState;

import static com.system.handlers.VarsMethods.initial_quantum;

public class SO {

    protected final Escalonador escalonador;

    private static int quantum = initial_quantum;

    public void subQuantum() {
        quantum = Math.max(0, Math.min(quantum - 1, quantum));
    }

    public void resetQuantum() {
        quantum = initial_quantum;
    }

    public static int getQuantum() {
        return quantum;
    }

    public SO(String str) {
        escalonador = new Escalonador(str);
    }

    public boolean error() {
        return (escalonador.getCurrentProcess().getState() == enumState.InvalidMemory ||
                escalonador.getCurrentProcess().getState() == enumState.InvalidInstructions);
    }

}
