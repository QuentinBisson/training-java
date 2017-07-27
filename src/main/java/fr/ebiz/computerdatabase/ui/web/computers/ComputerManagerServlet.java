package fr.ebiz.computerdatabase.ui.web.computers;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;
import fr.ebiz.computerdatabase.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/computers/*")
public class ComputerManagerServlet extends HttpServlet {

    private static final String CREATE_VIEW = "/WEB-INF/views/computers/add.jsp";
    private static final String EDIT_VIEW = "/WEB-INF/views/computers/edit.jsp";

    private static final String COMPUTER_ATTR = "computer";
    private static final String COMPANIES_ATTR = "companies";

    private static final String SELECTION_TO_DELETE_PARAM = "selection";

    @Autowired
    private ComputerService computerService;
    @Autowired
    private CompanyService companyService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter(ComputerValidator.COMPUTER_ID_FIELD);
        if (StringUtils.isBlank(idParam)) {
            newComputer(request, response);
        } else if (StringUtils.isNumeric(idParam)) {
            editComputer(request, response, idParam);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ids = request.getParameter(SELECTION_TO_DELETE_PARAM);

        if (!StringUtils.isBlank(ids)) {
            deleteComputers(ids.split(","));
            response.sendRedirect("home");
        } else {
            updateComputer(request, response);
        }
    }

    /**
     * Show the new computer form.
     *
     * @param request  The HTTP Request
     * @param response The HTTP Response
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an IO exception occurs
     */
    private void newComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(COMPUTER_ATTR, ComputerDto.builder().build());
        request.setAttribute(COMPANIES_ATTR, getAllCompanies());
        request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
    }

    /**
     * Show the edit form of a computer.
     *
     * @param request  The HTTP Request
     * @param response The HTTP Response
     * @param idParam  The id parameter identifying the computer to update
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an IO exception occurs
     */
    private void editComputer(HttpServletRequest request, HttpServletResponse response, String idParam) throws ServletException, IOException {
        Integer id = Integer.parseInt(idParam);
        Optional<ComputerDto> computerDto = computerService.get(id);
        if (computerDto.isPresent()) {
            request.setAttribute(COMPUTER_ATTR, computerDto.get());
            request.setAttribute(COMPANIES_ATTR, getAllCompanies());
            request.getRequestDispatcher(EDIT_VIEW).forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    /**
     * Create or update a computer.
     *
     * @param request  The HTTP Request
     * @param response The HTTP Response
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an IO exception occurs
     */
    private void updateComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ComputerForm form = new ComputerForm();
        Map<String, String> errors = form.parseAndValidate(request, response);

        ComputerDto dto = form.getModel();
        if (errors != null && errors.isEmpty()) {
            // Try to insert or update depending on business rules
            try {
                if (dto.getId() == null) {
                    computerService.insert(dto);
                } else {
                    computerService.update(dto);
                }
                response.sendRedirect(request.getContextPath());
            } catch (ValidationException e) {
                handleValidationErrors(request, response, dto, e.getErrors());
            }
        } else {
            handleValidationErrors(request, response, dto, errors);
        }
    }

    /**
     * Handle validation errors.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param model    The model being updated
     * @param errors   The map of errors
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an IO exception occurs
     */
    private void handleValidationErrors(HttpServletRequest request, HttpServletResponse response, ComputerDto model, Map<String, String> errors) throws ServletException, IOException {
        request.setAttribute("result", "Invalid form");
        request.setAttribute("errors", errors);
        request.setAttribute(COMPUTER_ATTR, model);

        request.setAttribute(COMPANIES_ATTR, getAllCompanies());
        request.getRequestDispatcher(model.getId() == null ? CREATE_VIEW : EDIT_VIEW).forward(request, response);
    }

    /**
     * Delete the computer ids.
     *
     * @param ids The list of id to delete
     */
    private void deleteComputers(String[] ids) {
        List<Integer> idList = new ArrayList<>();
        Arrays.stream(ids)
                .filter(StringUtils::isNumeric)
                .mapToInt(Integer::parseInt).forEach(idList::add);
        computerService.deleteComputers(idList);
    }

    /**
     * Get the list of all companies.
     *
     * @return the list of all companies
     */
    private List<Company> getAllCompanies() {
        return companyService.getAll(Pageable.builder().elements(Integer.MAX_VALUE).page(0).build()).getElements();
    }

}
