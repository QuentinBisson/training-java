package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.DashboardRequest;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao.OrderType;
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
    static final String ORDER_BY_PARAM = "order";
    private static final OrderType DEFAULT_ORDER = OrderType.NAME;
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
    static DashboardRequest parseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DashboardRequest.DashboardRequestBuilder builder = DashboardRequest.builder();

        parsePage(request, response, builder);
        parsePageSize(request, builder);
        parseOrderBy(request, builder);

        builder.query(request.getParameter(SEARCH_PARAM));
        return builder.build();
    }

    /**
     * Parse the page size parameter from the HTTP request.
     *
     * @param request the HTTP request
     * @param builder The page request builder
     */
    private static void parsePageSize(HttpServletRequest request, DashboardRequest.DashboardRequestBuilder builder) {
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
    private static void parsePage(HttpServletRequest request, HttpServletResponse response, DashboardRequest.DashboardRequestBuilder builder) throws IOException {
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
    private static void parseOrderBy(HttpServletRequest request, DashboardRequest.DashboardRequestBuilder builder) {
        builder.order(DEFAULT_ORDER);
        String parameter = request.getParameter(ORDER_BY_PARAM);
        Optional<OrderType> orderType = Arrays.stream(OrderType.values()).filter(order -> order.name().toLowerCase().equals(parameter)).findFirst();
        if (orderType.isPresent()) {
            builder.order(orderType.get());
        }
    }

}