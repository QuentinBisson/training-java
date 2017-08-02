package fr.ebiz.computerdatabase.ui.web.computer;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Qualifier("computerValidator")
public class ComputerValidator implements Validator {

    private static final String COMPUTER_INTRODUCED_FIELD = "introduced";
    private static final String COMPUTER_DISCONTINUED_FIELD = "discontinued";
    private static final String COMPUTER_COMPANY_FIELD = "company";

    private final CompanyService companyService;

    /**
     * Constructor.
     *
     * @param companyService The injected company Service
     */
    @Autowired
    public ComputerValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ComputerDto.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        ComputerDto computer = (ComputerDto) object;
        // We don't validate the name field since it is validated with annotations
        assertComputerIntroductionDateIsNullOrValid(errors, computer);
        assertComputerDiscontinuedDateIsNullOrValid(errors, computer);
        assertCompanyIsNullOrExist(errors, computer);

    }

    /**
     * Assert the computer's introduction date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertComputerIntroductionDateIsNullOrValid(Errors errors, ComputerDto computer) {
        if (computer.getIntroduced() != null && computer.getIntroduced().toEpochDay() < 0) {
            errors.rejectValue(COMPUTER_INTRODUCED_FIELD, "computers.constraints." + COMPUTER_INTRODUCED_FIELD + ".invalid");
        }
    }

    /**
     * Assert the computer's discontinuation date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertComputerDiscontinuedDateIsNullOrValid(Errors errors, ComputerDto computer) {
        if (computer.getDiscontinued() != null && computer.getDiscontinued().toEpochDay() < 0) {
            errors.rejectValue(COMPUTER_DISCONTINUED_FIELD, "computers.constraints." + COMPUTER_DISCONTINUED_FIELD + ".invalid");
        }

        if ((computer.getIntroduced() == null && computer.getDiscontinued() != null) || (computer.getIntroduced() != null && computer.getDiscontinued() != null && computer.getIntroduced().isAfter(computer.getDiscontinued()))) {
            errors.rejectValue(COMPUTER_DISCONTINUED_FIELD, "computers.constraints." + COMPUTER_DISCONTINUED_FIELD + ".afterIntroductionDate");
        }
    }

    /**
     * Assert the company id is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertCompanyIsNullOrExist(Errors errors, ComputerDto computer) {
        if (computer.getCompanyId() != null && !companyService.exists(computer.getCompanyId())) {
            errors.rejectValue(COMPUTER_COMPANY_FIELD, "computers.constraints." + COMPUTER_COMPANY_FIELD + ".invalid");
        }
    }


}
