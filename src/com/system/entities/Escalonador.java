package com.system.entities;

import com.system.handlers.enumState;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Escalonador {

    private Process[] Jobs;

    private int processControl = 0;

    private boolean end = false;

    public Escalonador() {

    }

    public void readJobs(String str) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(str))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray jobs = (JSONArray) obj;

            Jobs = new Process[jobs.size()];

            int i = 0;
            for(Object job : jobs) {
                Jobs[i] = new Process((JSONObject) job);
                i++;
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public int getProcessControl() {
        return processControl;
    }

    public void printJob() {
        Jobs[processControl].printAll();
    }

    public Process getCurrentProcess() {
        return Jobs[processControl];
    }

    public boolean isEnd() {
        return end;
    }

    public void unlockProcess(int i) {
        Jobs[i].blocked = false;
    }

    public void block() {
        Jobs[processControl].blocked = true;
    }

    public int nextJob() {
        int index = -1;
        double highest_priority = -1;
        this.end = true;
        for (int i = 0; i < Jobs.length; i++) {
            if (!Jobs[i].ended) {
                this.end = false;
            }
            if (!Jobs[i].blocked && Jobs[i].getPriority() > highest_priority) {
                index = i;
                highest_priority = Jobs[i].getPriority();
            }
        }

        if(index == processControl)
            return -1;
        if(index > -1)
            processControl = index;
        return index;

    }

    public void setJobEnd() {
        Jobs[processControl].ended = true;
    }

    public void setProcessNormal(int i) {
        this.Jobs[i].getRegisters().State = enumState.Normal;
    }
}
