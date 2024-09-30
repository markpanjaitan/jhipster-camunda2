package com.deloitte.service.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentDto {

    @NotNull
    private String assignee;

    private boolean allowOverrideAssignment;

    // Getters and Setters

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public boolean isAllowOverrideAssignment() {
        return allowOverrideAssignment;
    }

    public void setAllowOverrideAssignment(boolean allowOverrideAssignment) {
        this.allowOverrideAssignment = allowOverrideAssignment;
    }
}
