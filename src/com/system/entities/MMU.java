package com.system.entities;

import java.util.ArrayList;

public class MMU {

    private class PagesTable {
        ArrayList<Page> Pages;
        private final int sizePage;
        public PagesTable(int numPaginas, int tamPagina) {
            Pages = new ArrayList<Page>();
            for(int i=0; i<numPaginas; i++)
                Pages.add(new Page(i));
            this.sizePage = tamPagina;

        }

    }

    private PagesTable pagesTable;
    private FisicMemory fisicMemory;

    public MMU(FisicMemory fisicMemory) {
        this.fisicMemory = fisicMemory;
    }

    public int read(int index) {
        return fisicMemory.read(index);
    }

    public void save(int data, int index) {
        fisicMemory.change(data, index);
    }
}
