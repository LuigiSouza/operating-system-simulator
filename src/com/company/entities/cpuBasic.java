package com.company.entities;

import com.company.handlers.Tuple;
import com.company.handlers.enumCommands;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class cpuBasic {

    protected int PC;
    protected int Accumulator;

    protected boolean cpuStop;

    protected int[] memory;

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
    public void insertInstruction(String[] myString) {
        for(String v : myString)
            insertInstruction(v);
    }
    public void insertInstruction(String myString) {
        String[] parsed = myString.split(" ");

        if (parsed.length > 2)
            throwError();

        String cmd = parsed[0];
        enumCommands myEnum = tryEnum(cmd);

        if (parsed.length > 1)
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(), Integer.parseInt(parsed[1])));
        else
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(),null));
    }

    public Tuple<Integer, Integer> getInstruction(int PC) {
        return memoryInstructions.get(PC);
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
        throwError();
    }

    private interface instruction {
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

    private void throwError() {

        cpuStop = true;

        /*
        try {
            FileWriter myWriter = new FileWriter("Error_log.txt");

            myWriter.write("PC: " + PC + "\n");
            myWriter.write("Accumulator: " + Accumulator + "\n");
            myWriter.write("Memory: " + Arrays.toString(memory) + "\n");

            myWriter.close();

            throw new RuntimeException(
                    "Invalid instruction, error in command line on PC: " + PC + ". Error_log created.");

        } catch (IOException e) {
            System.out.println("Invalid instruction, error in command line on PC: " + PC + ". Error_log not created.");
            e.printStackTrace();
        }*/

    }

    public void execute() {
        Tuple<Integer, Integer> aux = getInstruction(PC);
        int i = aux.getX();
        Object n = aux.getY();

        if(i == 6 && n != null || i != 6 && n == null) {
            throwError();
            return;
        }
        PC++;
        getInstruction[i].execute(n);

        System.out.println("value Pc: " + PC + ", instruction type: " + enumCommands.values()[i] + ", A " + Accumulator+ ", memory " + Arrays.toString(memory));
    }

}
