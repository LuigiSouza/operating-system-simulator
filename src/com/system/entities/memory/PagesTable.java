package com.system.entities.memory;

import java.util.Arrays;

public class PagesTable {
    private final PageDescriber[] pageDescribers;
    private final int sizePage;
    public PagesTable(int numPages, int sizePage, int id) {
        pageDescribers = new PageDescriber[numPages];
        for(int i=0; i<numPages; i++)
            pageDescribers[i] = new PageDescriber(i, id);
        this.sizePage = sizePage;
    }

    public int convert(int address) {
        // get Page given an index and convert to physical address in memory
        return ((pageDescribers[address/sizePage].getFrame())*sizePage)+(address%sizePage);
    }

    protected PageDescriber getPage(int index) {
        return pageDescribers[index/sizePage];
    }

    public PageDescriber[] getPageDescribers() {
        return Arrays.copyOfRange(pageDescribers, 0 , pageDescribers.length);
    }

    public void describersAccessed(int address) {
        pageDescribers[address/sizePage].setAccessed(true);
    }

    public void describersChanged(int address) {
        pageDescribers[address/sizePage].setChanged(true);
    }

    public void describersChangeable(int address) {
        pageDescribers[address/sizePage].setChangeable(true);
    }

}
