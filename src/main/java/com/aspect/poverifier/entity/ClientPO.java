package com.aspect.poverifier.entity;

import java.math.BigDecimal;
import java.util.*;

public class ClientPO {
    private String number;
    private int position;
    private String amount;
    private BigDecimal totalAgreed;
    private String status;
    private String description;
    private boolean incorrect;
    private Set<Task> relatedTasks;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setAmount(String amount) {
        this.amount = amount;
        try{
            this.totalAgreed = new BigDecimal(amount);
        } catch (NumberFormatException ignored){}
    }

    public BigDecimal getTotalAgreed() {
        return totalAgreed;
    }

    public void setTotalAgreed(BigDecimal totalAgreed) {
        this.totalAgreed = totalAgreed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIncorrect() {
        return incorrect;
    }

    public void setIncorrect(boolean incorrect) {
        this.incorrect = incorrect;
    }

    public Set<Task> getRelatedTasks() {
        return relatedTasks;
    }

    public void setRelatedTasks(Set<Task> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    public void addRelatedTask(Task relatedTask) {
        if(this.relatedTasks != null) this.relatedTasks.add(relatedTask);
        else this.relatedTasks = new HashSet<>(Collections.singletonList(relatedTask));
    }

    public void addRelatedTask(Collection<Task> relatedTasks) {
        if(this.relatedTasks != null) this.relatedTasks.addAll(relatedTasks);
        else this.relatedTasks = new HashSet<>(relatedTasks);
    }
}
