<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <base href="${pageContext.request.contextPath}/">
    <!-- Bootstrap -->
    <link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="./css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="./css/main.css" rel="stylesheet" media="screen">
</head>
<body>
<c:import url="../../template/header.jsp"/>

<section id="main">
    <div class="container">
        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 box">
                <h1>Add Computer</h1>
                <form action="./computers" method="POST">
                    <c:out value="${result}"/>
                    <fieldset>
                        <div class="form-group">
                            <label for="computerName">Computer name</label>
                            <c:out value="${errors[\"name\"]}"/>
                            <input type="text" class="form-control" id="computerName" name="name"
                                   placeholder="Computer name" value="${computer.name}">
                        </div>
                        <div class="form-group">
                            <label for="introduced">Introduced date</label>
                            <c:out value="${errors[\"introduced\"]}"/>
                            <input type="date" class="form-control" id="introduced" name="introduced"
                                   placeholder="Introduced date"
                                   value="<tags:localDate pattern = "dd/MM/yyyy" value="${computer.introduced}" />">
                        </div>
                        <div class="form-group">
                            <label for="discontinued">Discontinued date</label>
                            <c:out value="${errors[\"discontinued\"]}"/>
                            <input type="date" class="form-control" id="discontinued" name="discontinued"
                                   placeholder="Discontinued date"
                                   value="<tags:localDate pattern = "dd/MM/yyyy" value="${computer.discontinued}" />">
                            <div class="form-group">
                                <label for="companyId">Company</label>
                                <c:out value="${errors[\"company\"]}"/>
                                <select class="form-control" id="companyId" name="company">
                                    <option value="0">--</option>
                                    <c:forEach var="company" items="${companies}">
                                        <option value="${company.id}"
                                                <c:if test="${company.id == computer.companyId}">selected</c:if>><c:out
                                                value="${company.name}"/></option>
                                    </c:forEach>
                                </select>
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
</body>
</html>