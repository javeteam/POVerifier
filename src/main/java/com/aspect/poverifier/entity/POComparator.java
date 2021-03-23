package com.aspect.poverifier.entity;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;

public class POComparator {
    private List<String> exclusiveXtrfPONumbers;
    private List<String> exclusiveClientPONumbers;
    private List<POIssue> POsWithProblems;
    private Map<String, List<Task>> xtrfTasksMap;
    private Map<String, BigDecimal> clientPONumbers;

    public List<String> getExclusiveXtrfPONumbers() {
        return exclusiveXtrfPONumbers;
    }

    public List<String> getExclusiveClientPONumbers() {
        return exclusiveClientPONumbers;
    }

    public List<POIssue> getPOsWithProblems() {
        return POsWithProblems;
    }

    public void compare(List<Task> xtrfTasks, Map<String, BigDecimal> clientPONumbers){
        init();
        if(xtrfTasks == null || clientPONumbers == null) return;
        this.clientPONumbers = new HashMap<>(clientPONumbers);
        for(Task task : xtrfTasks){
            for(String po : task.getClientProjectNames()){
                if(xtrfTasksMap.containsKey(po)){
                   xtrfTasksMap.get(po).add(task);
                }
                else xtrfTasksMap.put(po, new ArrayList<>(Collections.singletonList(task)));
            }
        }
        setExclusiveXtrfPONumbers();
        setExclusiveClientPONumbers();
        findPONumbersWithProblems();
    }

    /**
     * Looking for PO numbers which are present in XTRF but not in the list provided by client
     */
    private void setExclusiveXtrfPONumbers(){
        Set<String> keySet = xtrfTasksMap.keySet();
        for(String poNumber : keySet){
            if(!clientPONumbers.containsKey(poNumber)) exclusiveXtrfPONumbers.add(poNumber);
        }
    }

    /**
     *  Looking for PO numbers which are present in the list provided by client but not in XTRF
     */
    private void setExclusiveClientPONumbers(){
        Set<String> keySet = clientPONumbers.keySet();
        for(String po : keySet){
            if(!xtrfTasksMap.containsKey(po)) exclusiveClientPONumbers.add(po);
        }
    }

    /**
     * Get item from the list of POs provided by client. Then it is looking for all PO numbers which are related with this PO number in xtrf.
     * If sum of total agreed for all this PO numbers in client list and in xtrf is the same - this block is correct, so we delete all this items from clients list
     * and repeat this process with next item from client list. If sum isn't same this method creates POIssue and put difference value, all PO numbers found
     * in previous step and all related XTRF tasks. Then it deletes this PO numbers from client list and starts from next item. It works while client PO numbers list has items.
     */
    private void findPONumbersWithProblems(){
        Set<String> clientPONumbers = this.clientPONumbers.keySet();
        clientPONumbers.removeIf(po -> exclusiveClientPONumbers.contains(po));
        while (!clientPONumbers.isEmpty()){
            Iterator<String> iterator = clientPONumbers.iterator();
            Set<String> relatedPONumbers = getRelatedPONumbers(Collections.singleton(iterator.next()));
            if(relatedPONumbers.isEmpty()) iterator.remove();
            else {
                BigDecimal clientTotalAgreed = new BigDecimal("0");
                BigDecimal xtrfTotalAgreed = new BigDecimal("0");
                Set<Long> taskIds = new HashSet<>();
                for(String poNumber : relatedPONumbers){
                    if(this.clientPONumbers.containsKey(poNumber)) clientTotalAgreed = clientTotalAgreed.add(this.clientPONumbers.get(poNumber));
                    List<Task> tasks = xtrfTasksMap.get(poNumber);
                    for (Task task : tasks){
                        if(!taskIds.contains(task.getId())){
                            taskIds.add(task.getId());
                            xtrfTotalAgreed = xtrfTotalAgreed.add(task.getTotalAgreed());
                        }
                    }
                }
                if(xtrfTotalAgreed.compareTo(clientTotalAgreed) != 0){
                    POIssue issue = new POIssue();
                    issue.setDifference(xtrfTotalAgreed.subtract(clientTotalAgreed));
                    for(String poNumber : relatedPONumbers){
                        issue.addPoNumber(poNumber, xtrfTasksMap.get(poNumber));
                    }
                    POsWithProblems.add(issue);
                }
                clientPONumbers.removeAll(relatedPONumbers);
            }
        }
    }

    /**
     * Each client PO number related with one ore few xtrf projects. At the same time each xtrf project can contain few client PO numbers.
     * This method recursively looking for all intersections between PO numbers.
     *
     * @param clientPONumbers
     * It's set of POs which we are looking intersections with
     * At the beginning it's a set which contains only one PO number.
     * @return
     * Set of all POs which are connected with each other
     */

    private Set<String> getRelatedPONumbers(@NotNull Set<String> clientPONumbers){
        Set<String> relatedPONumbers = new HashSet<>();
        for(String poNumber : clientPONumbers){
            List<Task> poTasks = xtrfTasksMap.get(poNumber);
            for(Task task : poTasks){
                relatedPONumbers.addAll(task.getClientProjectNames());
            }
        }
        if(relatedPONumbers.size() > clientPONumbers.size()){
            relatedPONumbers = getRelatedPONumbers(relatedPONumbers);
        }
        return relatedPONumbers;
    }

    private void init(){
        exclusiveXtrfPONumbers = new ArrayList<>();
        exclusiveClientPONumbers = new ArrayList<>();
        POsWithProblems = new ArrayList<>();
        xtrfTasksMap = new HashMap<>();
    }

}
