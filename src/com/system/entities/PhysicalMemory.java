package com.system.entities;

import java.util.Arrays;

public class PhysicalMemory {

    private final int[] memory;
    private final int size_memory;
    private final int size_page;
    public PhysicalMemory(int size_memory, int size_page) {
        this.size_memory = size_memory;
        memory = new int[size_memory*size_page];
        this.size_page = size_page;
    }

    public int read(int index) {
        return memory[index];
    }

    public void change(int data,int index) {
        memory[index] = data;
    }

    public int getSize_memory() {
        return size_memory;
    }

    public int getSize_page() { return size_page; }

}