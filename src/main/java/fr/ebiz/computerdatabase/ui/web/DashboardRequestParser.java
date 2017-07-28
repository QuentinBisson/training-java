package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao.SortColumn;
import fr.ebiz.computerdatabase.persistence.dao.SortOrder;
import fr.ebiz.computerdatabase.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

class DashboardRequestParser {

    static final String PAGE_PARAM = "page";
    static final String SEARCH_PARAM = "search";
    static final String PAGE_SIZE_PARAM = "pageSize";
    static final String SORT_COLUMN_PARAM = "column";
    static final String SORT_ORDER_PARAM = "order";

    private static final SortColumn DEFAULT_COLUMN = SortColumn.NAME;
    private static final SortOrder DEFAULT_ORDER = SortOrder.ASC;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 0;

    /**
     * Parse the HTTP request for the dashboard servlet.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return The parsed request
     * @throws IOException if an error occurs when sending a 404 error
     */
    static GetAllComputersRequest parseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GetAllComputersRequest.GetAllComputersRequestBuilder builder = GetAllComputersRequest.builder();

        parsePage(request, response, builder);
        parsePageSize(request, builder);
        parseSortColumn(request, builder);
        parseSortOrder(request, builder);

        String query = request.getParameter(SEARCH_PARAM);
        builder.query(StringUtils.isBlank(query) ? null : query);
        return builder.build();
    }

    /**
     * Parse the page size parameter from the HTTP request.
     *
     * @param request the HTTP request
     * @param builder The page request builder
     */
    private static void parsePageSize(HttpServletRequest request, GetAllComputersRequest.GetAllComputersRequestBuilder builder) {
        String pageSizeParameter = request.getParameter(PAGE_SIZE_PARAM);
        builder.pageSize(DEFAULT_PAGE_SIZE);
        if (StringUtils.isNumeric(pageSizeParameter)) {
            builder.pageSize(Integer.valueOf(pageSizeParameter));
        }
    }

    /**
     * Parse the page parameter from the HTTP request.
     *
     * @param request  the HTTP request
     * @param response The HTTP response
     * @param builder  The page request builder
     * @throws IOException if an error occurs when sending a 404 error
     */
    private static void parsePage(HttpServletRequest request, HttpServletResponse response, GetAllComputersRequest.GetAllComputersRequestBuilder builder) throws IOException {
        builder.page(DEFAULT_PAGE);
        String pageParameter = request.getParameter(PAGE_PARAM);
        if (StringUtils.isNumeric(pageParameter)) {
            builder.page(Integer.valueOf(pageParameter));
        } else if (!StringUtils.isBlank(pageParameter)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Parse the order parameter from the HTTP request.
     *
     * @param request the HTTP request
     * @param builder The page request builder
     */
    private static void parseSortColumn(HttpServletRequest request, GetAllComputersRequest.GetAllComputersRequestBuilder builder) {
        builder.column(DEFAULT_COLUMN);
        String parameter = request.getParameter(SORT_COLUMN_PARAM);
        Optional<SortColumn> sortColumn = Arrays.stream(SortColumn.values()).filter(order -> order.name().toLowerCase().equals(parameter)).findFirst();
        if (sortColumn.isPresent()) {
            builder.column(sortColumn.get());
        }
    }

    /**
     * Parse the order parameter from the HTTP request.
     *
     * @param request the HTTP request
     * @param builder The page request builder
     */
    private static void parseSortOrder(HttpServletRequest request, GetAllComputersRequest.GetAllComputersRequestBuilder builder) {
        builder.order(DEFAULT_ORDER);
        String parameter = request.getParameter(SORT_ORDER_PARAM);
        Optional<SortOrder> sortOrder = Arrays.stream(SortOrder.values()).filter(order -> order.name().toLowerCase().equals(parameter)).findFirst();
        if (sortOrder.isPresent()) {
            builder.order(sortOrder.get());
        }
    }

}