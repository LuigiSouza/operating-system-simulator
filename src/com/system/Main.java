package com.system;

import com.system.GUI.Interface;
import com.system.entities.CPU;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        CPU cpu = new CPU(8);
        new Interface(cpu);


        /*
        CPU cpu = new CPU(128);
        String[] myString = new String[];= {
            "CARGI 10",
            "ARMM 2",
            "CARGI 32",
            "SOMA 2",
            "ARMM 0",
            "NEG",
            "PARA"
        };
        cpu.loadFile("filename.txt");

        cpu.executeAll();
        cpu.creteLog("text_log.txt"); */
	    // write your code here
    }
}
