package com.company.entities;

import com.company.handlers.Tuple;
import com.company.handlers.enumCommands;
import com.company.handlers.enumState;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * cpu_altera_programa = resetInstruction / insertInstruction
 * cpu_altera_dados = changeMemory / setMemory
 * cpu_salva_dados = getMemory
 * cpu_interrupcao = setCpuStop
 * cpu_retorna_interrupcao = isCpuStop
 * cpu_instrucao = getInstruction
 * cpu_salva_estado = getRegister
 * cpu_altera_estado = setRegister
 * cpu_estado_inicializa = CPU constructor
 * cpu_executa = execute / executeAll
 */
public class CPU extends cpuBasic {

    public CPU (Registers reg) {
        this.PC = reg.getPC();
        this.cpuStop = reg.getState();
        this.Accumulator = reg.getAccumulator();
    }

    public CPU (int i) {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
        this.memory = new int[i];
    }

    public CPU () {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
    }

    public void creteLog(String file) {
        try {

            FileWriter myWriter = new FileWriter(file + ".txt");

            myWriter.write("PC: " + this.PC + "\n");
            myWriter.write("Accumulator: " + this.Accumulator + "\n");
            myWriter.write("Memory: " + Arrays.toString(this.memory) + "\n");

            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred in file create.");
            e.printStackTrace();
        }
    }

    public void setCpuStopToNormal() {
        if ( cpuStop.getState() != 0 ) {
            this.cpuStop.setState(0);
            PC++;
        }
    }

    @Override
    public Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return super.getInstruction(PC);
        else
            return new Tuple<Integer, Integer>(enumCommands.ERROR.getCommand(), null);
    }

    public void resetInstructions(String[] myString) {
        super.clearInstructions();
        this.insertInstruction(myString);
    }


    public void changeMemory(int[] n){
        for (int i = 0; i < n.length && i < this.memory.length; i++)
            this.memory[i] = n[i];
    }

    public void setMemory(int[] i) {
        this.memory = i;
    }

    public int[] getMemory() {
        return memory;
    }

    public int getAccumulator() {
        return Accumulator;
    }

    public int getPC() {
        return PC;
    }

    public void setRegister(Registers reg) {
        this.PC = reg.getPC();
        this.Accumulator = reg.getAccumulator();
        this.cpuStop = reg.getState();
    }

    public Registers getRegisters() {
        return new Registers(getPC(), getAccumulator(), cpuStop);
    }

    public void setCpuStop(int i) {
        cpuStop.setState(i);
    }

    public boolean isCpuStop() {
        return cpuStop.getState() != 0;
    }

    public void executeAll() {
        while( !this.isCpuStop() ) {
            this.execute();
        }
    }
}
