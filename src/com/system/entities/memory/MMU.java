package com.system.entities.memory;

import com.system.entities.hardware.PhysicalMemory;

public class MMU {

    private PagesTable pagesTable;
    private final PhysicalMemory physicalMemory;
    private int page_fault_error = -1;

    public MMU(PhysicalMemory physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public PageDescriber getPage(int i) {
        return pagesTable.getPage(i/physicalMemory.getSize_page());
    }

    public void setPagesTableChangeable() {
        for (PageDescriber pag : pagesTable.getPageDescribers())
            pag.setChangeable(true);
    }

    public void changePagesTable(PagesTable pagesTable) {
        this.pagesTable = pagesTable;
    }

    public int read(int index) {
        pagesTable.describersAccessed(index);
        return physicalMemory.read(pagesTable.convert(index));
    }

    public void write(int data, int index) {
        physicalMemory.write(data, pagesTable.convert(index));
        pagesTable.describersAccessed(index);
        pagesTable.describersChanged(index);
    }

    public int getPage_fault_error() {
        return this.page_fault_error;
    }

    public int check(int index) {

        int sizeMemory = physicalMemory.getSize_memory();

        if(!getPage(index).isValid()) {
            System.out.println("Page Fault");
            page_fault_error = index;
            return 0;
        }
        else if (index >= sizeMemory * physicalMemory.getSize_page()) {
            System.out.println("Out of bounds");
            return -1;
        }

        return 1;
    }

}
