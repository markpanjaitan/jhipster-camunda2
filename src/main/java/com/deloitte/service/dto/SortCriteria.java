package com.deloitte.service.dto;

public class SortCriteria {

    private String field;
    private String order;

    // Default constructor
    public SortCriteria() {}

    // Parameterized constructor
    public SortCriteria(String field, String order) {
        this.field = field;
        this.order = order;
    }

    // Getters and Setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
