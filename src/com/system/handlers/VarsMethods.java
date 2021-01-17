package com.system.handlers;

import java.util.Vector;

public class VarsMethods {

    public static final int initial_quantum = 15;

    public static final int periodic_pause = -2;

    public static final long start = System.nanoTime();

    public static String output = "";

    public static String[] mySplit(String str, String regex) {
        Vector<String> result = new Vector<>();
        int start = 0;
        int pos = str.indexOf(regex);
        while (pos>=start) {
            if (pos>start) {
                result.add(str.substring(start,pos));
            }
            start = pos + regex.length();
            pos = str.indexOf(regex,start);
        }
        if (start<str.length()) {
            result.add(str.substring(start));
        }
        return result.toArray(new String[0]);
    }

    public static enumCommands tryEnum (String myString) {
        try {
            return Enum.valueOf(enumCommands.class, myString);
        } catch (IllegalArgumentException e) {
            // log error or something here
            return Enum.valueOf(enumCommands.class, "ERROR");
        }
    }
}
