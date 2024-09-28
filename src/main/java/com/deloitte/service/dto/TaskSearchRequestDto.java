package com.deloitte.service.dto;

import java.util.List;

public class TaskSearchRequestDto {

    private List<SortCriteria> sort;
    private int pageSize;
    private String state;

    // Default constructor
    public TaskSearchRequestDto() {}

    // Parameterized constructor
    public TaskSearchRequestDto(List<SortCriteria> sort, int pageSize, String state) {
        this.sort = sort;
        this.pageSize = pageSize;
        this.state = state;
    }

    // Getters and Setters
    public List<SortCriteria> getSort() {
        return sort;
    }

    public void setSort(List<SortCriteria> sort) {
        this.sort = sort;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
