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
                <div class="label label-default pull-right">
                    id: ${computer.id}
                </div>
                <h1>Edit Computer</h1>

                <form action="./computers" method="POST">
                    <c:out value="${result}"/>

                    <input type="hidden" name="id" value="${computer.id}" id="id"/>
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
                        </div>
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
                        <input type="submit" value="Edit" class="btn btn-primary">
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