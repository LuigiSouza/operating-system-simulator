package com.company;

import com.company.entities.CPU;
import com.company.assets.enumCommands;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner (System.in);
        String myString = sc.next();

        enumCommands myEnum = (enumCommands)Enum.valueOf(enumCommands.class, myString);
        System.out.println(myEnum.getCommand());

        CPU cpu = new CPU();
        System.out.println(cpu.execute(myEnum.getCommand()));
	// write your code here
    }
}
