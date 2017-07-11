package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.DashboardRequest;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/index"})
public class DashboardServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/dashboard.jsp";
    private static final String COMPUTERS_ATTR = "computers";

    private final ComputerService computerService;

    /**
     * Constructor.
     */
    public DashboardServlet() {
        this.computerService = ComputerServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DashboardRequest dashboardRequest = DashboardRequestParser.parseRequest(request, response);

        request.setAttribute(DashboardRequestParser.SEARCH_PARAM, dashboardRequest.getQuery());
        request.setAttribute(DashboardRequestParser.PAGE_SIZE_PARAM, dashboardRequest.getPageSize());
        request.setAttribute(DashboardRequestParser.PAGE_PARAM, dashboardRequest.getPage());
        request.setAttribute(DashboardRequestParser.ORDER_BY_PARAM, dashboardRequest.getOrder().name().toLowerCase());
        request.setAttribute(COMPUTERS_ATTR, computerService.getAll(dashboardRequest));

        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
