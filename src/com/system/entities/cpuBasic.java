package com.system.entities;

import com.system.handlers.Tuple;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class cpuBasic {

    protected int[][] IO;
    protected int[] counter;

    protected int PC;
    protected int Accumulator;


    protected enumState cpuStop;

    protected int[] memory;
    private int sizeProgram;

    /**
     *  First Integer: instruction index
     *  Second Integer: argument (if needed)
     */
    private final ArrayList<Tuple<Integer, Integer>> memoryInstructions = new ArrayList<>();


    public void insertInstruction(String[] myString) {
        for(String v : myString)
            insertInstruction(v);
        setSizeProgram();
    }
    public void insertInstruction(String myString) {
        String[] parsed = mySplit(myString, " ");

        if (parsed.length > 2) {
            System.out.println(Arrays.toString(parsed));
            setCpuStop(enumState.InvalidInstructions);
        }
        String cmd = parsed[0];
        enumCommands myEnum = tryEnum(cmd);

        if (parsed.length > 1)
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(), Integer.parseInt(parsed[1])));
        else
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(), null));

        setSizeProgram();
    }

    protected Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return memoryInstructions.get(PC);
        else {
            this.PC--;
            return new Tuple<>(enumCommands.ERROR.getCommand(), null);
        }
    }

    // Instructions function -------------------------------------

    private void CARGI(int n) {
        Accumulator = n;
    }
    private void CARGM(int n) {
        if(n < memory.length)
            Accumulator = memory[n];
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void CARGX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                Accumulator = memory[memory[n]];
                return;
            }
        }
        setCpuStop(enumState.InvalidMemory);
    }
    private void ARMM(int n) {
        if(n < memory.length)
            memory[n] = Accumulator;
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void ARMX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                memory[memory[n]] = Accumulator;
                return;
            }
        }
        setCpuStop(enumState.InvalidMemory);
    }
    private void SOMA(int n) {
        if(n < memory.length)
            Accumulator += memory[n];
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void NEG() {
        Accumulator *= -1;
    }
    private void DESVZ(int n) {
        if ( Accumulator == 0 ) {
            PC = n;
        }
    }
    private void PARA() {
        setCpuStop(enumState.Stop);
    }
    private void LE(int n) {
        Accumulator = IO[n][counter[n]];
        counter[n]++;
        setCpuStop(enumState.Read);
    }
    private void GRAVA(int n) {
        IO[n][counter[n]] = Accumulator;
        counter[n]++;
        setCpuStop(enumState.Save);
    }
    private void ERROR() {
        setCpuStop(enumState.InvalidInstructions);
        PC--;
    }

    private interface instruction {
        void execute(Object i);
    }

    private final instruction[] getInstruction = new instruction[] {
            n -> CARGI((int) n),
            n -> CARGM((int) n),
            n -> CARGX((int) n),
            n -> ARMM((int) n),
            n -> ARMX((int) n),
            n -> SOMA((int) n),
            n -> NEG(),
            n -> DESVZ((int) n),
            n -> PARA(),
            n -> LE((int) n),
            n -> GRAVA((int) n),
            n -> ERROR(),
    };

    // Instructions function -------------------------------------

    // Sees if an instruction has mandatory argument
    public static boolean hasArgument(int i) {
        return i != 6 && i != 8 && i != 11;
    }

    public void execute() {
        if(isCpuStop())
            return;

        Tuple<Integer, Integer> aux;
        if (PC < memoryInstructions.size())
            aux = memoryInstructions.get(PC);
        else {
            setCpuStop(enumState.InvalidInstructions);
            return;
        }
        int i = aux.getX();
        Object n = aux.getY();

        System.out.println(PC + " : " + i + " " + n);

        if((!hasArgument(i) && n != null ) || (hasArgument(i) && n == null)) {
            setCpuStop(enumState.InvalidInstructions);
            return;
        }
        PC++;
        getInstruction[i].execute(n);

    }

    public void popInstruction() {
        if (getSizeProgram() <= 0)
            return;
        memoryInstructions.remove(memoryInstructions.get(memoryInstructions.size()-1));
        setSizeProgram();
        if(PC >= getSizeProgram() && PC > 0)
            PC--;
    }

    public void clearInstructions() {
        memoryInstructions.clear();
        setSizeProgram();

        PC = 0;
        Accumulator=0;
        setCpuStop(enumState.Normal);
    }

    protected int getSizeProgram() { return sizeProgram; }

    protected void setSizeProgram() { sizeProgram = memoryInstructions.size(); }

    // Returns all instructions into String format
    public String getInstructionsToString() {
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for(Tuple<Integer, Integer> v : memoryInstructions){
            ret.append(i).append(": ").append(enumCommands.values()[v.getX()]).append(" ");
            if (v.getY() == null)
                ret.append('\n');
            else
                ret.append(v.getY()).append('\n');
            i++;
        }
        return ret.toString();
    }

    public void setCpuStop(enumState i) {
        cpuStop = i;
    }

    public boolean isCpuStop() { return cpuStop != enumState.Normal; }

    private static String[] mySplit(String str, String regex) {
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

    private enumCommands tryEnum (String myString) {
        try {
            return Enum.valueOf(enumCommands.class, myString);
        } catch (IllegalArgumentException e) {
            // log error or something here
            return Enum.valueOf(enumCommands.class, "ERROR");
        }
    }
}
