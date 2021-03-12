package com.system.entities.memory;

import com.system.entities.hardware.PhysicalMemory;

public class MMU {

    private PagesTable pagesTable;
    private final PhysicalMemory physicalMemory;

    public MMU(PhysicalMemory physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public void changePagesTable(PagesTable pagesTable) {
        this.pagesTable = pagesTable;
    }

    public int read(int index) {
        pagesTable.describersAccessed(index);
        return physicalMemory.read(pagesTable.convert(index));
    }

    public void write(int data, int index) {
        pagesTable.describersAccessed(index);
        pagesTable.describersChanged(index);
        physicalMemory.write(data, pagesTable.convert(index));
    }

    public int check(int index) {

        int sizeMemory = physicalMemory.getSize_memory();
        int sizePage = physicalMemory.getSize_page();

        int indexPage = index/sizePage;

        if(!pagesTable.getPage(indexPage).isValid())
            return 0;
        else if (!pagesTable.getPage(indexPage).is_changeable() || index >= sizeMemory*sizePage)
            return -1;

        return 1;

    }

}
