package com.company.entities;

import com.company.handlers.Tuple;
import com.company.handlers.enumCommands;

import java.util.ArrayList;

public class CPU {

    private int PC = 0;
    private int Accumulator = 0;

    private int[] memory = new int[1024];

    /**
     *  First Integer: instruction index
     *  Second Integer: argument (if needed)
     */
    private ArrayList<Tuple<Integer, Integer>> memoryInstructions = new ArrayList<>();

    private enumCommands tryEnum (String myString) {
        try {
            enumCommands myEnum = (enumCommands)Enum.valueOf(enumCommands.class, myString);
            return myEnum;
        } catch (IllegalArgumentException e) {
            // log error or something here
            return (enumCommands)Enum.valueOf(enumCommands.class, "ERROR");
        }
    }

    public void insertInstruction(String myString) {
        enumCommands myEnum = tryEnum(myString);
        memoryInstructions.add(new Tuple<>(myEnum.getCommand(),null));
    }

    public void insertInstruction(String myString, int n) {
        enumCommands myEnum = tryEnum(myString);
        memoryInstructions.add(new Tuple<>(myEnum.getCommand(), n));
    }


    private void CARGI(int n) {
        Accumulator = n;
    }
    private void CARGM(int n) {
        memory[n] = Accumulator;
    }
    private void CARGX(int n) {
        Accumulator = memory[memory[n]];
    }
    private void ARMM(int n) {
        memory[n] = Accumulator;
    }
    private void ARMX(int n) {
        memory[memory[n]] = Accumulator;
    }
    private void SOMA(int n) {
        Accumulator += memory[n];
    }
    private void NEG() {
        Accumulator *= -1;
    }
    private void DESVZ(int n) {
        if ( Accumulator == 0 ) {
            PC = n;
        }
    }
    private void ERROR() {
        throw new RuntimeException(
                "Invalid instruction, nonexistent, CPU interrupted");
    }

    interface instruction {
        void execute(Object i);
    }

    private final instruction[] getInstruction = new instruction[] {
            n -> CARGI((int) n),
            n -> CARGM((int) n),
            n -> CARGX((int) n),
            n -> ARMM((int) n),
            n -> ARMX((int) n),
            n -> SOMA((int) n),
            n -> NEG(),
            n -> DESVZ((int) n),
            n -> ERROR(),
    };

    public void execute() {

        int i = memoryInstructions.get(PC).getX();
        Object n = memoryInstructions.get(PC).getY();

        if(i == 6 && n != null)
            throw new RuntimeException(
                    "Invalid instruction, received argument to NEG instruction, CPU interrupted");
        else if(i != 6 && n == null)
            throw new RuntimeException(
                    "Invalid instruction, missing argument to command, CPU interrupted");

        PC++;
        if ( n != null )
            getInstruction[i].execute((int) n);
        else
            getInstruction[i].execute(n);
    }
}
