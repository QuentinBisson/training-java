<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                <h1><spring:message code="computers.actions.edit"/></h1>
                <form:form action="${contextPath}/computers/${computer.id}" method="POST" id="computer-form"
                           modelAttribute="computer">
                    <form:hidden path="id"/>
                    <fieldset>
                        <spring:bind path="name">
                            <div class="form-group ${status.error ? 'has-danger' : 'has-success'}">
                                <spring:message var="fieldName" code="computers.field.name"/>
                                <form:label path="name" cssClass="form-control-label">${fieldName}</form:label>

                                <form:input
                                        id="computerName"
                                        path="name"
                                        placeholder="${fieldName}"
                                        cssClass="form-control ${status.error ? 'form-control-danger' : 'form-control-success'}"
                                />
                                <div class="form-control-error">
                                    <form:errors path="name"/>
                                </div>
                            </div>
                        </spring:bind>

                        <spring:bind path="introduced">
                            <div class="form-group ${status.error ? 'has-danger' : 'has-success'}">
                                <spring:message code="computers.field.introduced" var="fieldIntroduced"/>
                                <form:label path="introduced"
                                            cssClass="form-control-label">${fieldIntroduced}</form:label>

                                <form:input
                                        id="introduced"
                                        type="date"
                                        path="introduced"
                                        class="form-control ${status.error ? 'form-control-danger' : 'form-control-success'}"
                                        placeholder="${fieldIntroduced}"
                                />
                                <div class="form-control-error">
                                    <form:errors path="introduced"/>
                                </div>
                            </div>
                        </spring:bind>

                        <spring:bind path="discontinued">
                            <div class="form-group ${status.error ? 'has-danger' : 'has-success'}">
                                <spring:message code="computers.field.discontinued" var="fieldDiscontinued"/>
                                <form:label path="discontinued"
                                            cssClass="form-control-label">${fieldDiscontinued}</form:label>

                                <form:input
                                        id="discontinued"
                                        type="date"
                                        path="discontinued"
                                        cssClass="form-control ${status.error ? 'form-control-danger' : 'form-control-success'}"
                                        placeholder="${fieldDiscontinued}"
                                />

                                <div class="form-control-error">
                                    <form:errors path="discontinued"/>
                                </div>
                            </div>
                        </spring:bind>

                        <spring:bind path="companyId">
                            <div class="form-group ${status.error ? 'has-danger' : 'has-success'}">
                                <form:label path="companyId" cssClass="form-control-label"><spring:message
                                        code="computers.field.company"/></form:label>

                                <form:select
                                        id="companyId"
                                        path="companyId"
                                        cssClass="form-control ${status.error ? 'form-control-danger' : 'form-control-success'}">
                                    <form:option value="" label="--"/>
                                    <form:options items="${companies}" itemValue="id" itemLabel="name"/>
                                </form:select>

                                <div class="form-control-error">
                                    <form:errors path="companyId"/>
                                </div>
                            </div>
                        </spring:bind>
                    </fieldset>
                    <div class="actions pull-right">
                        <form:button type="submit" class="btn btn-primary"><spring:message
                                code="actions.edit"/></form:button>
                        <spring:message code="globals.or"/>
                        <a href="${contextPath}" class="btn btn-default"><spring:message code="actions.cancel"/></a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript">
    // Declare variable here to access spring i18n in js code
    var i18nFromJava = {
        computers: {
            constraints: {
                name: {
                    toosmall: '<spring:message code="computers.constraints.name.toosmall" arguments="3"/>'
                },
                introduced: {
                    invalid: '<spring:message code="computers.constraints.introduced.invalid" />'
                },
                discontinued: {
                    invalid: '<spring:message code="computers.constraints.discontinued.invalid" />',
                    afterIntroductionDate: '<spring:message code="computers.constraints.discontinued.afterIntroductionDate" />'
                },
                company: {
                    invalid: '<spring:message code="computers.constraints.company.invalid" />'
                }
            }
        }
    }
</script>

<script src="${contextPath}/js/jquery.min.js"></script>
<script src="${contextPath}/js/computers.js"></script>
</body>
</html>