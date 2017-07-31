package fr.ebiz.computerdatabase.ui.web;

import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.ui.web.binding.CaseInsensitiveConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;

@Controller
@RequestMapping({"/", "home", "dashboard"})
public class DashboardController extends HttpServlet {

    private static final String COMPUTERS_ATTR = "computers";
    private static final String DASHBOARD_VIEW = "dashboard";

    private final ComputerService computerService;

    /**
     * Constructor.
     *
     * @param computerService The injected computer service
     */
    public DashboardController(ComputerService computerService) {
        this.computerService = computerService;
    }

    /**
     * Get the dashboard view.
     *
     * @param model   The request model
     * @param request The computer request
     * @return the model and view
     */
    @GetMapping
    public ModelAndView getDashboard(@ModelAttribute GetAllComputersRequest request, ModelAndView model) {
        model.setViewName(DASHBOARD_VIEW);
        model.addObject("request", request);
        model.addObject(COMPUTERS_ATTR, computerService.getAll(request));
        return model;
    }

    /**
     * Init the bindings for enumerations.
     *
     * @param binder The web binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(SortOrder.class, new CaseInsensitiveConverter<>(SortOrder.class));
        binder.registerCustomEditor(ComputerDao.SortColumn.class, new CaseInsensitiveConverter<>(ComputerDao.SortColumn.class));
    }

}
