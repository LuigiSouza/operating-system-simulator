package com.company.entities;

import com.company.assets.enumCommands;

import java.util.ArrayList;

public class CPU {

    private ArrayList<enumCommands> memoryInstructions = new ArrayList<>();

    public int CARGI() { return 0; }
    public int CARGM() { return 11; }
    public int CARGX() { return 22; }
    public int ARMM() { return 33; }
    public int ARMX() { return 44; }
    public int SOMA() { return 55; }
    public int NEG() { return 66; }
    public int DESVZ() { return 77; }

    interface instruction {
        int execute();
    }

    private final instruction[] getInstruction = new instruction[] {
            this::CARGI,
            this::CARGM,
            this::CARGX,
            this::ARMM,
            this::ARMX,
            this::SOMA,
            this::NEG,
            this::DESVZ,
    };

    public int execute(int index) {
        return getInstruction[index].execute();
    }
}
