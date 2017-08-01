package fr.ebiz.computerdatabase.ui.web.computer;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.ui.web.exception.ResourceNotFoundException;
import fr.ebiz.computerdatabase.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/computers")
public class ComputerController {

    private static final String DELETE_COMPUTERS_REGEX = "\\d+(,\\d+)*";

    private static final String COMPUTER_ATTR = "computer";
    private static final String COMPANIES_ATTR = "companies";

    private static final String ADD_COMPUTER_VIEW = "computers/add";
    private static final String EDIT_COMPUTER_VIEW = "computers/edit";
    private static final String REDIRECT_TO_DASHBOARD_VIEW = "redirect:/dashboard";

    @Autowired
    private ComputerService computerService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    @Qualifier("computerValidator")
    private Validator validator;

    /**
     * Init the validators.
     *
     * @param binder The web binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
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
    public String saveComputer(@Valid @ModelAttribute("computer") ComputerDto computerDto, BindingResult result) {
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
     * Create or update a computer.
     *
     * @param ids The list of id to delete
     * @return The created model
     */
    @PostMapping("/delete")
    public String deleteComputers(
            @RequestParam("selection")
            @Pattern(regexp = DELETE_COMPUTERS_REGEX) String ids) {
        List<Integer> idList = new ArrayList<>();
        Arrays.stream(ids.split(","))
                .filter(StringUtils::isNumeric)
                .mapToInt(Integer::parseInt).forEach(idList::add);

        computerService.deleteComputers(idList);
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
        return companyService.getAll(Pageable.builder().elements(Integer.MAX_VALUE).page(0).build()).getElements();
    }


}
