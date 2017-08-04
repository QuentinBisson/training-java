package fr.ebiz.computerdatabase.ui.web.computer;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.ui.web.converter.LocalDatePropertyEditorSupport;
import fr.ebiz.computerdatabase.ui.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/computers")
public class ComputerController {

    private static final String COMPUTER_ATTR = "computer";
    private static final String COMPANIES_ATTR = "companies";

    private static final String ADD_COMPUTER_VIEW = "computers/add";
    private static final String EDIT_COMPUTER_VIEW = "computers/edit";
    private static final String REDIRECT_TO_DASHBOARD_VIEW = "redirect:/dashboard";

    private final ComputerService computerService;
    private final CompanyService companyService;
    private final ComputerValidator validator;
    private final LocalDatePropertyEditorSupport localDatePropertyEditorSupport;

    /**
     * Constructor.
     *
     * @param computerService                The computer service
     * @param companyService                 The company service
     * @param validator                      The computer validator
     * @param localDatePropertyEditorSupport localDatePropertyEditorSupport
     */
    @Autowired
    public ComputerController(ComputerService computerService, CompanyService companyService, ComputerValidator validator, LocalDatePropertyEditorSupport localDatePropertyEditorSupport) {
        this.computerService = computerService;
        this.companyService = companyService;
        this.validator = validator;
        this.localDatePropertyEditorSupport = localDatePropertyEditorSupport;
    }

    /**
     * Init the validators.
     *
     * @param binder The web binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
        binder.registerCustomEditor(LocalDate.class, localDatePropertyEditorSupport);
    }

    /**
     * Get the add computer form.
     *
     * @param model The spring model
     * @return The created model
     */
    @GetMapping
    public String addComputer(Model model) {
        model.addAttribute(COMPUTER_ATTR, ComputerDto.builder().build());
        model.addAttribute(COMPANIES_ATTR, getAllCompanies());
        return ADD_COMPUTER_VIEW;
    }

    /**
     * Get the add computer form.
     *
     * @param computerId The computerId
     * @param model      The request parameters
     * @return The created model
     */
    @GetMapping("/{computerId}")
    public String editComputer(@PathVariable("computerId") int computerId, Model model) {
        computerService.get(computerId)
                .map(computer -> fillModelToEdit(model, computer))
                .orElseThrow(ResourceNotFoundException::new);

        return EDIT_COMPUTER_VIEW;
    }

    /**
     * Create or update a computer.
     *
     * @param computerDto The Computer DTO
     * @param result      The validation results
     * @return The created model
     */
    @PostMapping
    public String saveComputer(@ModelAttribute("computer") @Valid ComputerDto computerDto, BindingResult result) {
        if (result.hasErrors()) {
            if (computerDto.getId() == null) {
                return ADD_COMPUTER_VIEW;
            } else {
                return EDIT_COMPUTER_VIEW;
            }
        }

        // Try to insert or update depending on business rules
        if (computerDto.getId() == null) {
            computerService.insert(computerDto);
        } else {
            computerService.update(computerDto);
        }
        return REDIRECT_TO_DASHBOARD_VIEW;
    }

    /**
     * Fill the model to edit a computer.
     *
     * @param model    The model to fill
     * @param computer The computer to edit
     * @return computer    The computer
     */
    private ComputerDto fillModelToEdit(Model model, ComputerDto computer) {
        model.addAttribute(COMPUTER_ATTR, computer);
        model.addAttribute(COMPANIES_ATTR, getAllCompanies());
        return computer;
    }

    /**
     * Get the list of all companies.
     *
     * @return the list of all companies
     */
    private List<Company> getAllCompanies() {
        return companyService.getAll(Pageable.builder().pageSize(Integer.MAX_VALUE).page(0).build()).getElements();
    }


}
