package com.system.entities.hardware;

import java.util.Arrays;

public class PhysicalMemory {

    private final int[] memory;
    private final int size_memory;
    private final int size_page;
    public PhysicalMemory(int size_memory, int size_page) {
        this.size_memory = size_memory; // numero de quadros
        this.size_page = size_page; // tamanhos do quadro
        memory = new int[size_memory*size_page];
    }

    public int read(int index) {
        //System.out.println("Reading " + memory[index] + " from " + index + " address");
        return memory[index];
    }

    public int[] read_page(int i) {
        int index = i*size_page;
        return Arrays.copyOfRange(memory, index, index+size_page);
    }

    public void write(int data, int index) {
        //System.out.println("Writing " + data + " at " + index + " address over " + memory[index]);
        memory[index] = data;
    }

    public void write_page(int[] data_page, int index) {
        for(int i = index*size_page, j=0; i<index*size_page+size_page; i++,j++)
            memory[i] = data_page[j];
    }

    public int getSize_memory() {
        return size_memory;
    }

    public int getSize_page() { return size_page; }

}