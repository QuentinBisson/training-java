package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class DashboardServlet extends HttpServlet {

    private static final int DEFAULT_ELEMENTS_PER_PAGE = 10;
    private static final int DEFAULT_PAGE = 0;

    private static final String VIEW = "/WEB-INF/views/dashboard.jsp";
    private static final String PAGE_PARAM = "page";
    private static final String ELEMENTS_PER_PAGE_PARAM = "elements";
    private static final String SEARCH_PARAM = "search";
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
        String pageParameter = request.getParameter(PAGE_PARAM);
        int page = DEFAULT_PAGE;
        if (StringUtils.isNumeric(pageParameter)) {
            page = Integer.valueOf(pageParameter);
        }

        String elementsPerPageParameter = request.getParameter(ELEMENTS_PER_PAGE_PARAM);
        int elementsPerPage = DEFAULT_ELEMENTS_PER_PAGE;
        if (StringUtils.isNumeric(elementsPerPageParameter)) {
            elementsPerPage = Integer.valueOf(elementsPerPageParameter);
        }

        String query = request.getParameter(SEARCH_PARAM);
        request.setAttribute(COMPUTERS_ATTR, computerService.getAll(query, Pageable.builder().page(page).elements(elementsPerPage).build()));
        request.setAttribute(SEARCH_PARAM, query);
        request.setAttribute(ELEMENTS_PER_PAGE_PARAM, elementsPerPageParameter);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
