<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<header class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <spring:message var="appTitle" code="app.title"/>
        <tags:link className="navbar-brand col-md-6" content="${appTitle}"/>
        <div class="col-md-6">
            <div class="pull-right">
                <tags:link paramName="lang" paramValue="en" content="EN" className="btn btn-link"/>
                <tags:link paramName="lang" paramValue="fr" content="FR" className="btn btn-link"/>

                <sec:authorize access="isAuthenticated()">
                    <form action="${pageContext.request.contextPath}/logout" method="POST" style="display: inline;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-link"><spring:message code="app.logout"/></button>
                    </form>
                </sec:authorize>
            </div>
        </div>
    </div>
</header>