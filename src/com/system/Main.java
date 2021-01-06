package com.system;

import com.system.entities.CPU;
import com.system.entities.SO;

public class Main {

    public static void main(String[] args) {

        SO so = new SO(1, 3);
        so.getCpu().loadFile("filename.txt");
        so.start();

	    // write your code here
    }
}
