package com.company;

import com.company.entities.CPU;
import com.company.handlers.enumCommands;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner (System.in);
        String[] myString = {
            "CARGI 10",
            "ARMM 2",
            "CARGI 32",
            "SOMA 2",
            "ARMM 0",
            "PARA"
        };

        //int myInt = sc.nextInt();

        CPU cpu = new CPU(56);
        cpu.insertInstruction(myString);
        while( !cpu.isCpuStop() )
            cpu.execute();
	// write your code here
    }
}
