package com.company;

import com.company.entities.CPU;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {

        CPU cpu = new CPU(128);
        /*String[] myString = new String[];= {
            "CARGI 10",
            "ARMM 2",
            "CARGI 32",
            "SOMA 2",
            "ARMM 0",
            "NEG",
            "PARA"
        };*/
        try {
            File myObj = new File("filename.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                cpu.insertInstruction(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        cpu.executeAll();
        cpu.creteLog("text_log");
	    // write your code here
    }
}
