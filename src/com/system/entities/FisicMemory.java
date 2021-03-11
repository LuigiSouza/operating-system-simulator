package com.system.entities;

import java.util.Arrays;

public class FisicMemory {

    private final int[] memory;
    private final int size_memory;
    private final int size_page;

    public FisicMemory(int size_memory, int size_page) {
        this.size_memory = size_memory;
        this.size_page = size_page;
        memory = new int[size_memory*size_page];
    }

    public int read(int index) {
        return memory[index];
    }

    public int[] read_page(int i) {
        int index = i*size_page;
        return Arrays.copyOfRange(memory, index, index+size_page);
    }

    public void change(int data,int index) {
        memory[index] = data;
    }

    public void change_page(int[] data_page, int index) {
        for(int i = index*size_page, j=0; i<index*size_page+size_page; i++,j++)
            memory[i] = data_page[j];
    }

    public int getSize_memory() {
        return size_memory;
    }

    public int getSize_page() {
        return size_page;
    }
}