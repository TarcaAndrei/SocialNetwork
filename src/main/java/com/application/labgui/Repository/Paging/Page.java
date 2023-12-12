package com.application.labgui.Repository.Paging;

public class Page<E> {
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }

    private Iterable<E> elementsOnPage;
    private int totalNumberOfElements;

    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements){
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }
}
