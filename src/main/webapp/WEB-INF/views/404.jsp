<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <base href="${pageContext.request.contextPath}/"/>
    <!-- Bootstrap -->
    <link href="${contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/main.css" rel="stylesheet" media="screen">
</head>
<body>

<c:import url="../template/header.jsp"/>

<section id="main">
    <div class="container">
        <div class="alert alert-danger">
            <spring:message code="error.404"/>
            <br/>
            <!-- stacktrace -->
        </div>
    </div>
</section>

<script src="${contextPath}/js/jquery.min.js"></script>
<script src="${contextPath}/js/bootstrap.min.js"></script>
<script src="${contextPath}/js/dashboard.js"></script>

</body>
</html>