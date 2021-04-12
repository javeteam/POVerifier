package com.aspect.poverifier.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(projectIdNumber, task.projectIdNumber) &&
                projectManager.equals(task.projectManager) &&
                totalAgreed.equals(task.totalAgreed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectIdNumber, projectManager, totalAgreed);
    }
}
