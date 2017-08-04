<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<header class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <a class="navbar-brand" href="${contextPath}"> <spring:message code="app.title"/> </a>
        <div class="pull-right">
            <spring:url var="currentUrl" value=""/>
            <c:set var="queryString"
                   value='${pageContext.request.queryString.replaceAll("[?&]{0,1}lang=[a-zA-Z]{2}", "")}'/>
            <c:set var="langUrl" value='${currentUrl}?${queryString}${empty queryString ? "lang=" : "&lang="}'/>
            <a href="${langUrl}en">
                EN
            </a>
            <a href="${langUrl}fr">
                FR
            </a>
        </div>
    </div>
</header>