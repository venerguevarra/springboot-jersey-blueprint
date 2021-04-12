package com.doesitwork.springboot.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class PagedResultBean<T> {
    private int page;
    private int size;
    private long totalElements;
    private long totalResultElements;
    private List<T> elements;
    private int totalPages;

    public PagedResultBean() {
        this.page = 0;
        this.size = 0;
        this.totalElements = 0;
        this.elements = new ArrayList<>();
        this.totalPages = 0;
        this.totalResultElements = 0;
    }

    public PagedResultBean(Page<T> page) {
        this.elements = page.getContent();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalResultElements = this.elements.size();
    }

    public PagedResultBean(int page, int size, long totalElements, List<T> elements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.elements = elements;
        this.totalPages = totalPages;
        this.totalResultElements = this.elements.size();
    }
}
