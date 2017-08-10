<%@ tag body-content="empty" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="paramName" required="false" type="java.lang.String" %>
<%@ attribute name="paramValue" required="false" type="java.lang.Object" %>
<%@ attribute name="className" required="false" type="java.lang.String" %>
<%@ attribute name="content" required="false" type="java.lang.String" %>

<spring:url var="currentUrl" value=""/>

<c:if test="${empty paramName && empty paramValue}">
    <c:set var="resultUrl" value="${currentUrl}?${pageContext.request.queryString}"/>
</c:if>
<c:if test="${!empty paramName && !empty paramValue}">
    <c:set var="queryString"
           value='${pageContext.request.queryString.replaceAll("[?&]{0,1}".concat(paramName).concat("=[a-zA-Z]{2}"), "")}'/>

    <c:choose>
        <c:when test="${empty queryString}">
            <c:set var="resultUrl"
                   value="${currentUrl.concat('?').concat(queryString).concat(paramName).concat('=').concat(paramValue)}"/>
        </c:when>
        <c:otherwise>
            <c:set var="resultUrl"
                   value="${currentUrl.concat('?').concat(queryString).concat('&').concat(paramName).concat('=').concat(paramValue)}"/>
        </c:otherwise>
    </c:choose>
</c:if>

<a href="${resultUrl}" class="${className}">${content}</a>