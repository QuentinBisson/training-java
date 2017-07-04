package fr.ebiz.computerdatabase.ui.web.computers;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.CompanyServiceImpl;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;
import fr.ebiz.computerdatabase.service.validator.impl.ComputerValidator;
import fr.ebiz.computerdatabase.utils.FormUtils;
import fr.ebiz.computerdatabase.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
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

    private ComputerService computerService;
    private CompanyService companyService;

    /**
     * Constructor.
     */
    public ComputerManagerServlet() {
        computerService = ComputerServiceImpl.getInstance();
        companyService = CompanyServiceImpl.getInstance();
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
            response.sendRedirect(request.getContextPath());
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
        ComputerDto.ComputerDtoBuilder builder = ComputerDto.builder();
        try {

            Map<String, String> errors = new HashMap<>();

            String idParam = request.getParameter(ComputerValidator.COMPUTER_ID_FIELD);
            if (StringUtils.isNumeric(idParam)) {
                builder.id(Integer.parseInt(idParam));
            } else if (!StringUtils.isBlank(idParam)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            builder.name(request.getParameter(ComputerValidator.COMPUTER_NAME_FIELD));

            String introducedParam = request.getParameter(ComputerValidator.COMPUTER_INTRODUCED_FIELD);
            LocalDate introduced = FormUtils.getLocalDate(introducedParam);
            if (!StringUtils.isBlank(introducedParam) && introduced != null) {
                builder.introduced(introduced);
            } else if (!StringUtils.isBlank(introducedParam)) {
                errors.put(ComputerValidator.COMPUTER_INTRODUCED_FIELD, "Introduction is not a valid date !");
            }

            String discontinuedParam = request.getParameter(ComputerValidator.COMPUTER_DISCONTINUED_FIELD);
            LocalDate discontinued = FormUtils.getLocalDate(discontinuedParam);
            if (!StringUtils.isBlank(discontinuedParam) && discontinued != null) {
                builder.discontinued(discontinued);
            } else if (!StringUtils.isBlank(discontinuedParam)) {
                errors.put(ComputerValidator.COMPUTER_DISCONTINUED_FIELD, "Discontinuation is not a valid date !");
            }

            String companyParam = request.getParameter(ComputerValidator.COMPUTER_COMPANY_FIELD);
            if (StringUtils.isNumeric(companyParam)) {
                builder.companyId(Integer.parseInt(companyParam));
            } else if (!StringUtils.isBlank(companyParam)) {
                errors.put(ComputerValidator.COMPUTER_COMPANY_FIELD, "Company id is not valid !");
            }

            if (errors.isEmpty()) {
                ComputerDto dto = builder.build();
                // Try to insert or update depending on business rules

                if (dto.getId() == null) {
                    computerService.insert(dto);
                } else {
                    computerService.update(dto);
                }

                response.sendRedirect(request.getContextPath());
            } else {
                handleValidationErrors(request, response, builder.build(), errors);
            }
        } catch (ValidationException e) {
            handleValidationErrors(request, response, builder.build(), e.getErrors());
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
        Arrays.stream(ids)
                .filter(StringUtils::isNumeric)
                .mapToInt(Integer::parseInt)
                .forEach(id -> computerService.delete(ComputerDto.builder().id(id).build()));
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
