<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Computer Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <base href="${pageContext.request.contextPath}/"/>
    <!-- Bootstrap -->
    <link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="./css/font-awesome.css" rel="stylesheet" media="screen">
    <link href="./css/main.css" rel="stylesheet" media="screen">
</head>
<body>
<c:import url="../template/header.jsp"/>

<section id="main">
    <div class="container">
        <div class="alert alert-danger">
            Error 500: An error has occured!
            <br/>
            <!-- stacktrace -->
        </div>
    </div>
</section>

<script src="../../js/jquery.min.js"></script>
<script src="../../js/bootstrap.min.js"></script>
<script src="../../js/dashboard.js"></script>

</body>
</html>