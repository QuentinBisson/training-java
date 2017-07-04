<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <!-- Bootstrap -->
    <link href="${contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="${contextPath}/css/main.css" rel="stylesheet" media="screen">
</head>
<body>
<c:import url="../../template/header.jsp"/>

<section id="main">
    <div class="container">
        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 box">
                <h1>Add Computer</h1>
                <form action="./computers" method="POST" id="computer-form">
                    <c:out value="${result}"/>
                    <fieldset>
                        <div class="form-group <c:if test='${!empty errors["name"]}'>has-danger</c:if> <c:if test='${!empty errors && empty errors["name"]}'>has-success</c:if>">
                            <label class="form-control-label" for="computerName">Computer name</label>
                            <input type="text"
                                   class="form-control  <c:if test='${!empty errors["name"]}'>form-control-danger</c:if> <c:if test='${!empty errors && empty errors["name"]}'>form-control-success</c:if>"
                                   id="computerName" name="name"
                                   placeholder="Computer name" value="${computer.name}">
                            <div class="form-control-error"><c:out value="${errors[\"name\"]}"/></div>
                        </div>
                        <div class="form-group <c:if test='${!empty errors["introduced"]}'>has-danger</c:if> <c:if test='${!empty errors && empty errors["introduced"]}'>has-success</c:if>">
                            <label class="form-control-label" for="introduced">Introduced date</label>
                            <input type="date"
                                   class="form-control <c:if test='${!empty errors["introduced"]}'>form-control-danger</c:if> <c:if test='${!empty errors && empty errors["introduced"]}'>form-control-success</c:if>"
                                   id="introduced" name="introduced"
                                   placeholder="Introduced date"
                                   value="<tags:localDate pattern = "dd/MM/yyyy" value="${computer.introduced}" />">
                            <div class="form-control-error"><c:out value="${errors[\"introduced\"]}"/></div>
                        </div>
                        <div class="form-group <c:if test='${!empty errors["discontinued"]}'>has-danger</c:if> <c:if test='${!empty errors && empty errors["discontinued"]}'>has-success</c:if>">
                            <label class="form-control-label" for="discontinued">Discontinued date</label>
                            <input type="date"
                                   class="form-control <c:if test='${!empty errors["discontinued"]}'>form-control-danger</c:if> <c:if test='${!empty errors && empty errors["discontinued"]}'>form-control-success</c:if>"
                                   id="discontinued" name="discontinued"
                                   placeholder="Discontinued date"
                                   value="<tags:localDate pattern = "dd/MM/yyyy" value="${computer.discontinued}" />">
                            <div class="form-control-error"><c:out value="${errors[\"discontinued\"]}"/></div>
                        </div>
                        <div class="form-group <c:if test='${!empty errors["company"]}'>has-danger</c:if> <c:if test='${!empty errors && empty errors["company"]}'>has-success</c:if>">
                            <label class="form-control-label" for="companyId">Company</label>
                            <select class="form-control <c:if test='${!empty errors["company"]}'>form-control-danger</c:if> <c:if test='${!empty errors && empty errors["company"]}'>form-control-success</c:if>"
                                    id="companyId" name="company">
                                <option value="">--</option>
                                <c:forEach var="company" items="${companies}">
                                    <option value="${company.id}"
                                            <c:if test="${company.id == computer.companyId}">selected</c:if>><c:out
                                            value="${company.name}"/></option>
                                </c:forEach>
                            </select>
                            <div class="form-control-error"><c:out value="${errors[\"company\"]}"/></div>
                        </div>
                    </fieldset>
                    <div class="actions pull-right">
                        <input type="submit" value="Add" class="btn btn-primary">
                        or
                        <a href="." class="btn btn-default">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>
<script src="${contextPath}/js/jquery.min.js"></script>
<script src="${contextPath}/js/computers.js"></script>
</body>
</html>