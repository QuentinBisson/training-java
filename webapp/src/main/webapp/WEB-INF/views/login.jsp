<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8">
    <!-- Bootstrap -->
    <link href="${contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/main.css" rel="stylesheet" media="screen">
</head>
<body>

<c:import url="../template/header.jsp"/>

<section id="main">
    <div class="container">
        <h1 id="homeTitle"><spring:message code="login.title"/></h1>
        <div class="container" style="margin-top: 10px;">
            <form action="login" method='POST'>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <fieldset>
                    <div class="form-group">
                        <spring:message var="username" code="login.username"/>
                        <label class="form-control-label">${username}</label>
                        <input name="username"
                               placeholder="${username}"
                               class="form-control"
                        />
                    </div>

                    <div class="form-group">
                        <spring:message var="password" code="login.password"/>
                        <label class="form-control-label">${password}</label>
                        <input name="password"
                               type="password"
                               placeholder="${password}"
                               class="form-control"
                        />
                    </div>
                </fieldset>
                <div class="actions pull-right">
                    <button class="btn btn-primary"><spring:message code="login.button"/></button>
                </div>
            </form>
        </div>
    </div>
</section>
</body>
</html>