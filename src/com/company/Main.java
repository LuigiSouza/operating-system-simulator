package com.company;

import com.company.entities.CPU;
import com.company.handlers.enumCommands;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner (System.in);
        String myString = sc.next();
        int myInt = sc.nextInt();

        CPU cpu = new CPU();
        cpu.insertInstruction(myString);
        //System.out.println(cpu.execute());
	// write your code here
    }
}
