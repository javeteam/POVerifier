package com.aspect.poverifier.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Task {
    private long id;
    private String projectIdNumber;
    private String projectManager;
    private Set<String> clientProjectNames = new HashSet<>();
    private BigDecimal totalAgreed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectIdNumber() {
        return projectIdNumber;
    }

    public void setProjectIdNumber(String projectIdNumber) {
        this.projectIdNumber = projectIdNumber;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public Set<String> getClientProjectNames() {
        return clientProjectNames;
    }

    public void setClientProjectNames(Set<String> clientProjectNames) {
        this.clientProjectNames = clientProjectNames;
    }

    public BigDecimal getTotalAgreed() {
        return totalAgreed;
    }

    public void setTotalAgreed(BigDecimal totalAgreed) {
        this.totalAgreed = totalAgreed;
    }
}
