package com.company;

import com.company.entities.CPU;
import com.company.entities.cpuBasic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
        cpu.loadFile("filename.txt");

        cpu.executeAll();
        cpu.creteLog("text_log.txt");
	    // write your code here
    }
}
