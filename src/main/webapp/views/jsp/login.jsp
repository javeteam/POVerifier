<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>PO Verifier login</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/common.css"/>
    </head>
    <body>

    <form class="login_form" action="${pageContext.request.contextPath}/j_spring_security_check" method="post">
        <input id="login" type="text" name="j_login" autocomplete="off" value="" readonly placeholder="username" required/>
        <div class="delimiter"></div>
        <input id="password" type="password" name="j_password" autocomplete="off" value="" readonly placeholder="password" required/>
        <div class="button"></div>
    </form>

    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/page-notifications.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/select2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/common.js"></script>
    </body>
</html>

