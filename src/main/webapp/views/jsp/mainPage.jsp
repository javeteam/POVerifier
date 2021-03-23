<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>PO Verifier</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/common.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/page-notifications.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/select2.css"/>
    </head>
    <body>
    <div class="wrapper">
        <div class="menu" data-url="${pageContext.request.contextPath}/compare">
            <form class="csv_section" action="${pageContext.request.contextPath}/addCSV">
                <div class="section_column">
                    <div class="section_column_item">
                        <input type="file" accept=".csv" name="csvFile" placeholder="Select file to compare with" required>
                    </div>
                    <div class="section_column_item">
                        <a href="${pageContext.request.contextPath}/getCSVTemplate">Download file template</a>
                    </div>
                </div>
            </form>
            <form class="xtrf_section" action="${pageContext.request.contextPath}/addXTRFProperties">
                <div class="section_column">
                    <div class="section_column_item">
                        <label class="item_title" for="dateFrom">Date from:</label>
                        <input type="date" id="dateFrom" name="dateFrom" required>
                    </div>
                    <div class="section_column_item">
                        <label class="item_title" for="dateTo">Date till:</label>
                        <input type="date" id="dateTo" name="dateTo" required>
                    </div>
                </div>
                <div class="section_column">
                    <div class="section_column_item">
                        <label class="item_title" for="po_delimiter">Delimiter:</label>
                        <select id="po_delimiter" name="delimiter" required>
                            <option value="BOTH">Coma or Space</option>
                            <option value="COMA">Coma</option>
                            <option value="SPACE">Space</option>
                        </select>
                    </div>
                    <div class="section_column_item">
                        <label class="item_title" for="customerId">Customer:</label>
                        <select id="customerId" name="customerId" required>
                            <option></option>
                            <c:forEach var="customer" items="${customers}">
                                <option value="${customer.id}">${customer.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </form>
            <div class="statistic_section">
                <div class="section_column js_csv_info empty">
                    <span>CSV row count:</span>
                    <span class="value">0</span>
                </div>
                <div class="section_column js_xtrf_info empty">
                    <span>XTRF row count:</span>
                    <span class="value">0</span>
                </div>
            </div>
        </div>

        <div class="document"></div>
    </div>

    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/page-notifications.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/select2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/common.js"></script>
    </body>
</html>

