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
        <h1 id="homeTitle">
            <c:out value="${computers.totalElements}"/> <spring:message code="computers.found"/>
        </h1>
        <div id="actions" class="form-horizontal">
            <div class="pull-left">
                <form id="searchForm" action="#" method="GET" class="form-inline">
                    <input type="search" id="searchbox" name="query" class="form-control"
                           placeholder="<spring:message code="computers.search.name" />"
                           value="${request.query}"/>
                    <input type="submit" id="searchsubmit"
                           value="<spring:message code="computers.actions.filterByName" />"
                           class="btn btn-primary"/>
                </form>
            </div>
            <div class="pull-right">
                <a class="btn btn-success" id="addComputer" href="${contextPath}/computers"><spring:message
                        code="computers.actions.add"/></a>
                <a class="btn btn-default" id="editComputer" href="#" onclick="$.fn.toggleEditMode();"><spring:message
                        code="actions.edit"/></a>
            </div>
        </div>
    </div>

    <form id="deleteForm" action="${contextPath}/computers/delete" method="POST">
        <input type="hidden" name="selection" value="">
        <input type="hidden" name="query" value="${request.query}">
        <input type="hidden" name="page" value="${computers.currentPage}">
        <input type="hidden" name="pageSize" value="${request.pageSize}">
        <input type="hidden" name="column" value="${request.column}">
    </form>

    <div class="container" style="margin-top: 10px;">
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <!-- Variable declarations for passing labels as parameters -->
                <!-- Table header for Computer Name -->

                <th class="editMode" style="width: 60px; height: 22px;">
                    <input type="checkbox" id="selectall"/>
                    <span style="vertical-align: top;">
                         -  <a href="#" id="deleteSelected">
                                <i class="fa fa-trash-o fa-lg"></i>
                            </a>
                    </span>
                </th>
                <th>
                    <a class="${(empty request.column || "name".equalsIgnoreCase(request.column)) ? 'disabled' : ''}"
                       href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=name">
                        <spring:message code="computers.field.name"/>
                    </a>
                </th>
                <th>
                    <a class="${"introduced".equalsIgnoreCase(request.column) ? 'disabled' : '' }"
                       href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=introduced">
                        <spring:message code="computers.field.introduced"/>
                    </a>
                </th>
                <!-- Table header for Discontinued Date -->
                <th>
                    <a class="${"discontinued".equalsIgnoreCase(request.column) ? 'disabled' : '' }"
                       href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=discontinued">
                        <spring:message code="computers.field.discontinued"/>
                    </a>
                </th>
                <!-- Table header for Company -->
                <th>
                    <a class="${"company".equalsIgnoreCase(request.column) ? 'disabled' : '' }"
                       href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=company">
                        <spring:message code="computers.field.company"/>
                    </a>
                </th>
            </tr>

            </thead>
            <!-- Browse attribute computers -->
            <tbody id="results">
            <c:forEach items="${computers.elements}" var="computer">
                <tr>
                    <td class="editMode">
                        <input type="checkbox" name="cb" class="cb" value="${computer.id}">
                    </td>
                    <td>
                        <a href="computers/${computer.id}" onclick=""><c:out value="${computer.name}"/></a>
                    </td>
                    <td>
                        <c:if test="${!empty computer.introduced}">
                            <tags:localDate value="${computer.introduced}"/>
                        </c:if>
                    </td>
                    <td><c:if test="${!empty computer.discontinued}">
                        <tags:localDate value="${computer.discontinued}"/>
                    </c:if></td>
                    <td><c:out value="${computer.companyName}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<footer class="navbar-fixed-bottom">
    <div class="container text-center">
        <tags:pager
                page="${computers.currentPage}"
                totalPages="${computers.totalPages}"
                pageSize="${request.pageSize}"
                url="?query=${request.query}&column=${request.column}"/>
    </div>
</footer>

<script type="text/javascript">
    // Declare variable here to access spring i18n in js code
    var i18nFromJava = {
        actions: {
            view: '<spring:message code="actions.view" />',
            edit: '<spring:message code="actions.edit" />',
        },
        computers: {
            actions: {
                confirmDeletion: '<spring:message code="computers.actions.confirmDeletion" />'
            }
        }
    }
</script>
<script src="${contextPath}/js/jquery.min.js"></script>
<script src="${contextPath}/js/bootstrap.min.js"></script>
<script src="${contextPath}/js/dashboard.js"></script>
</body>
</html>