package com.system.entities.os;

import com.system.handlers.VarsMethods;
import com.system.handlers.enumState;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Scheduler {

    private Process[] Jobs;

    private int processControl = 0;

    private int next_job = 0;

    // benchmark
    protected int time_cpu = 0;

    protected int changes = 0;
    protected int preemption_times = 0;
    // -------------

    private final int initial_quantum;

    private boolean end = false;

    public Scheduler(String str, int initial_quantum) {
        this.initial_quantum = initial_quantum;

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(str))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray jobs = (JSONArray) obj;

            Jobs = new Process[jobs.size()];

            for(int i = 0; i < jobs.size(); i++) {
                Jobs[i] = new Process((JSONObject) jobs.get(i), i);
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

    public int getJobSize() { return Jobs.length; }

    public int getCurrentJobIndex() {
        return next_job;
    }

    public Process getCurrentProcess() {
        if (Jobs[processControl].date_release < 0) {
            Jobs[processControl].date_release = SO.timer.getTimer();
        }
        return Jobs[processControl];
    }

    public boolean isEnd() {
        return end;
    }

    public void deal_interruption(int update) {

        Jobs[update].subInterruption();

        if(Jobs[update].getInterruption() > 0)
            return;

        setProcessNormal(update);
        unlockProcess(update);
    }

    private void setProcessNormal(int i) {
        this.Jobs[i].getRegisters().setState(enumState.Normal);
    }

    private void unlockProcess(int i) {
        Jobs[i].blocked = false;
    }

    public void block() {
        Jobs[processControl].blocked = true;
    }

    public void calculate_priority() {
        Jobs[processControl].setPriority((Jobs[processControl].getPriority() + (1d - ((double) SO.getQuantum()/initial_quantum)))/2d);
    }

    public void loadNext() {
        processControl = next_job;
        //calculate_priority();
    }

    public int nextJob() {

        int index = -1;
        double highest_priority = 2;
        this.end = true;

        double temp = Jobs[processControl].getPriority();
        Jobs[processControl].setPriority(2d);

        for (int i = 0; i < Jobs.length; i++) {
            if (!Jobs[i].ended)
                this.end = false;
            if (!Jobs[i].blocked && Jobs[i].getPriority() < highest_priority) {
                index = i;
                highest_priority = Jobs[i].getPriority();
            }
        }

        Jobs[processControl].setPriority(temp);

        if(index == processControl)
            return -1;
        if(index > -1) {
            next_job = index;
            changes++;
        }

        return index;

    }

    public void setJobEnd(){
        Jobs[processControl].ended = true;
    }

    public void printResults() {
        for(int i = 0; i < Jobs.length; i++) {
            Process job = Jobs[i];
            VarsMethods.output += "\nBenchmark Process " + i + ":\n";
            VarsMethods.output += "Hora de Inicio: " + job.date_release + "\n";
            VarsMethods.output += "Hora de Termino: " + job.date_end + "\n";
            VarsMethods.output += "Tempo de Retorno: " + (job.date_end - job.date_release) + "\n";
            VarsMethods.output += "Tempo de CPU: " + job.time_cpu + "\n";
            VarsMethods.output += "Percentual de CPU: " + (job.time_cpu*100/this.time_cpu) + "% em relacao ao timer\n";
            VarsMethods.output += "Tempo Bloqueado: " + job.time_blocked + "\n";
            VarsMethods.output += "Vezes Bloqueado: " + job.blocked_times + "\n";
            VarsMethods.output += "Vezes Escalonado: " + job.times_schedule + "\n";
            VarsMethods.output += "Numero de vezes que a CPU foi perdida: " + job.times_lost + "\n";
            preemption_times += job.times_lost;
        }
    }

    public void print_Jobs_IO() {
        for(Process job : Jobs) {
            System.out.println("JOBS IO:");
            int[][] IO = job.getIO();
            for (int[] i : IO) {
                System.out.println(Arrays.toString(i));
            }
        }
    }
}
