package fr.ebiz.computerdatabase.web;

import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.web.converter.CaseInsensitiveConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/", "home", "dashboard"})
public class DashboardController {

    private static final String DELETE_COMPUTERS_REGEX = "\\d+(,\\d+)*";

    private static final String COMPUTERS_ATTR = "computers";
    private static final String DASHBOARD_VIEW = "dashboard";

    private final ComputerService computerService;

    /**public
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
    public String getDashboard(@ModelAttribute GetAllComputersRequest request, Model model) {
        model.addAttribute("request", request);
        model.addAttribute(COMPUTERS_ATTR, computerService.getAll(request));
        return DASHBOARD_VIEW;
    }


    /**
     * Create or update a computer.
     *
     * @param request            The query parameters
     * @param ids                The list of id to delete
     * @param redirectAttributes The redirect attributes
     * @return a redirection to the dashboard
     */
    @PostMapping("/computers/delete")
    public String deleteComputers(GetAllComputersRequest request,
                                  @RequestParam("selection")
                                  @Pattern(regexp = DELETE_COMPUTERS_REGEX) String ids, RedirectAttributes redirectAttributes) {
        List<Integer> idList = new ArrayList<>();
        Arrays.stream(ids.split(","))
                .mapToInt(Integer::parseInt).forEach(idList::add);

        computerService.deleteComputers(idList);

        redirectAttributes.addFlashAttribute(request);
        return "redirect:/" + DASHBOARD_VIEW;
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
