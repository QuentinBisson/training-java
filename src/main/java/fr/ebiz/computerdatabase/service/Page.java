package fr.ebiz.computerdatabase.service;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    private List<T> elements;
    private int totalPages;
    private int currentPage;

    public Page() {
        elements = new ArrayList<>();
    }

    public List<T> getElements() {
        return elements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
