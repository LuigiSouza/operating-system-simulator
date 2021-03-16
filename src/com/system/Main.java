package com.system;

import com.system.entities.os.SO;

public class Main {

    public static void main(String[] args) {

        int SIZE_MEM = 40;
        int SIZE_PAGE = 2;
        int NUM_PAGE = 20;
        int INTERRUPTION_WRITE = 2;
        int INTERRUPTION_CLEAN = 4;
        boolean SECOND_CHANCE = false;
        int initial_quantum = 6;
        String inFile = "testFiles_virtualMemory/in.json";
        String outFile = "testFiles_virtualMemory/fullmemory_defaultfifo.txt";

        SO so = new SO(
                inFile,
                SIZE_MEM,
                SIZE_PAGE,
                NUM_PAGE,
                INTERRUPTION_WRITE,
                INTERRUPTION_CLEAN,
                SECOND_CHANCE,
                initial_quantum
        );

        so.run();
        so.printBench(outFile);

    }
}
