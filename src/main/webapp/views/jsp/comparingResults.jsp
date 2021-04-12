<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="document">
        <c:if test="${exclusiveClientPOs.size() == 0 && exclusiveXtrfPOs.size() == 0 && POsWithProblems.size() == 0}">
            <span class="no_errors">No differences found</span>
        </c:if>
        <c:if test="${exclusiveClientPOs.size() != 0 || exclusiveXtrfPOs.size() != 0}">
            <div class="result_wrapper">
                <div class="exclusive_po_numbers">
                    <div class="header">Exclusive Client POs</div>
                    <c:forEach var="exclusiveClientPO" items="${exclusiveClientPOs}">
                        <span class="po_number">${exclusiveClientPO.number}</span>
                    </c:forEach>
                </div>
                <div class="exclusive_po_numbers">
                    <div class="header">Exclusive XTRF POs</div>
                    <c:forEach var="exclusiveXtrfPO" items="${exclusiveXtrfPOs}">
                        <span class="po_number">${exclusiveXtrfPO.number}</span>
                    </c:forEach>
                </div>
            </div>
        </c:if>
        <div class="po_problems_wrapper">
            <c:forEach var="problem" items="${POsWithProblems}">
                <table class="m-table">
                    <thead>
                    <tr>
                        <th colspan="3">Difference: ${problem.printDifference()}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="key" items="${problem.poNumbers.keySet()}" varStatus="loop">
                        <tr>
                            <td rowspan="${problem.poNumbers.get(key).size()}">${key}</td>
                            <c:forEach var="task" items="${problem.poNumbers.get(key)}" varStatus="loop">
                                <c:if test="${loop.index > 0}"><tr></c:if>
                                <td>${task.projectIdNumber}</td>
                                <td>${task.projectManager}</td>
                                <c:if test="${loop.index > 0}"></tr></c:if>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:forEach>
        </div>
    </div>


