<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
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
            <c:out value="${computers.totalElements}"/> Computers found
        </h1>
        <div id="actions" class="form-horizontal">
            <div class="pull-left">
                <form id="searchForm" action="#" method="GET" class="form-inline">
                    <input type="search" id="searchbox" name="query" class="form-control" placeholder="Search name"
                           value="${request.query}"/>
                    <input type="submit" id="searchsubmit" value="Filter by name"
                           class="btn btn-primary"/>
                </form>
            </div>
            <div class="pull-right">
                <a class="btn btn-success" id="addComputer" href="${contextPath}/computers">Add Computer</a>
                <a class="btn btn-default" id="editComputer" href="#" onclick="$.fn.toggleEditMode();">Edit</a>
            </div>
        </div>
    </div>

    <form id="deleteForm" action="${contextPath}/computers" method="POST">
        <input type="hidden" name="selection" value="">
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
                                 -  <a href="#" id="deleteSelected" onclick="$.fn.deleteSelected();">
                                        <i class="fa fa-trash-o fa-lg"></i>
                                    </a>
                            </span>
                </th>
                <th>
                    <a
                            <c:if test="${empty request.column || \"name\".equalsIgnoreCase(request.column)}">class="disabled"
                            </c:if>href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=name">Computer
                        name</a>
                </th>
                <th>
                    <a
                            <c:if test="${\"introduced\".equalsIgnoreCase(request.column)}">class="disabled" </c:if>
                            href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=introduced">Introduced
                        date</a>
                </th>
                <!-- Table header for Discontinued Date -->
                <th>
                    <a
                            <c:if test="${\"discontinued\".equalsIgnoreCase(request.column)}">class="disabled" </c:if>
                            href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=discontinued">Discontinued
                        date</a>
                </th>
                <!-- Table header for Company -->
                <th>
                    <a
                            <c:if test="${\"company\".equalsIgnoreCase(request.column)}">class="disabled" </c:if>
                            href="?query=${request.query}&page=${computers.currentPage}&pageSize=${request.pageSize}&column=company">Company</a>
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
                        <a href="computers?id=${computer.id}" onclick=""><c:out value="${computer.name}"/></a>
                    </td>
                    <td>
                        <c:if test="${!empty computer.introduced}">
                            <tags:localDate pattern="dd/MM/yyyy" value="${computer.introduced}"/>
                        </c:if>
                    </td>
                    <td><c:if test="${!empty computer.discontinued}">
                        <tags:localDate pattern="dd/MM/yyyy" value="${computer.discontinued}"/>
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

<script src="${contextPath}/js/jquery.min.js"></script>
<script src="${contextPath}/js/bootstrap.min.js"></script>
<script src="${contextPath}/js/dashboard.js"></script>
</body>
</html>