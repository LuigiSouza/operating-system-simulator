package com.system;

import com.system.entities.os.SO;

public class Main {

    public static void main(String[] args) {

        int SIZE_MEM = 20;
        int SIZE_PAGE = 2;
        int NUM_PAGE = 30;
        int INTERRUPTION_WRITE = 2;
        int INTERRUPTION_CLEAN = 2;
        boolean SECOND_CHANCE = false;
        int initial_quantum = 3;
        String inFile = "in2.json";
        String outFile = "out.txt";

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
