<%@ tag body-content="empty" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="page" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="elements" required="true" type="java.lang.Integer" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>

<c:if test="${empty elements}">
    <c:set var="elements" value="10" />
</c:if>

<c:if test="${!empty elements}">
    <c:set var="elementsParameter" value="&elements=${elements}" />
</c:if>
<c:set var="pageParameter" value="&page=${currentPage}" />

<ul class="pagination">
    <c:if test="${page > 0}">
        <li>
            <a href="${url}&page=${page - 1}${elementsParameter}" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
    </c:if>

    <c:forEach begin="${Math.max(0, page - 3)}" end="${Math.max(0, Math.min(totalPages - 1, page + 3))}" var="currentPage">
        <li <c:if test="${page == currentPage}"> class="active" </c:if>/>
            <a href="${url}&page=${currentPage}${elementsParameter}">
                <c:out value="${currentPage + 1}" />
            </a>
        </li>
    </c:forEach>

    <c:if test="${page < totalPages - 1}" >
        <li>
            <a href="${url}&page=${page + 1}${elementsParameter}" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </c:if>
</ul>

<div class="btn-group btn-group-sm pull-right" role="group">
    <a class="btn btn-default <c:if test="${elements == 10}"> btn-primary</c:if>" href="${url}&elements=10${pageParameter}">10</a>
    <a class="btn btn-default <c:if test="${elements == 50}"> btn-primary</c:if>" href="${url}&elements=50${pageParameter}">50</a>
    <a class="btn btn-default <c:if test="${elements == 100}"> btn-primary</c:if>" href="${url}&elements=100${pageParameter}">100</a>
</div>