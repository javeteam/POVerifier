package com.aspect.poverifier.entity;

import java.math.BigDecimal;
import java.util.*;

public class POIssue {
    private BigDecimal difference;
    private final Map<String, List<Task>> poNumbers = new HashMap<>();

    public String printDifference() {
        return difference.toPlainString();
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public Map<String, List<Task>> getPoNumbers() {
        return poNumbers;
    }

    public void addPoNumber(String poNumber, List<Task> t){
        List<Task> tasks = new ArrayList<>(t);
        Set<String> projectIdNumbers = new HashSet<>();
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()){
            Task task = iterator.next();
            if(projectIdNumbers.contains(task.getProjectIdNumber())) iterator.remove();
            else projectIdNumbers.add(task.getProjectIdNumber());
        }

        poNumbers.put(poNumber, tasks);
    }

}
