package com.doesitwork.springboot.bean;

import java.util.List;

public class ListResultBean<T> {
    private List<T> items;

    public ListResultBean(List<T> items) {
        this.items = items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }
}
