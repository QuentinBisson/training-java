package fr.ebiz.computerdatabase.service.validator.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.impl.CompanyServiceImpl;
import fr.ebiz.computerdatabase.service.validator.AbstractValidator;
import fr.ebiz.computerdatabase.service.validator.Validator;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ComputerValidator extends AbstractValidator<ComputerDto> {

    public static final String COMPUTER_ID_FIELD = "id";
    public static final String COMPUTER_NAME_FIELD = "name";
    public static final String COMPUTER_INTRODUCED_FIELD = "introduced";
    public static final String COMPUTER_DISCONTINUED_FIELD = "discontinued";
    public static final String COMPUTER_COMPANY_FIELD = "company";

    private CompanyService companyService;

    /**
     * Constructor.
     *
     * @param companyService The injected company service
     */
    private ComputerValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Get the validator instance.
     *
     * @return The singleton instance
     */
    public static Validator<ComputerDto> getInstance() {
        return Singleton.INSTANCE.getValidator();
    }

    @Override
    public void validate(ComputerDto computer) {
        Map<String, String> errors = new HashMap<>();
        assertComputerNameIsFilled(errors, computer);
        assertComputerIntroductionDateIsNullOrValid(errors, computer);
        assertComputerDiscontinuedDateIsNullOrValid(errors, computer);
        assertCompanyIsNullOrExist(errors, computer);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Assert the computer's name is filled and not blank, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The error map
     * @param computer The computer to test
     */
    private void assertComputerNameIsFilled(Map<String, String> errors, ComputerDto computer) {
        if (computer.getName() == null || computer.getName().trim().isEmpty()) {
            addError(errors, COMPUTER_NAME_FIELD, "Name should be filled !");
        }
    }

    /**
     * Assert the computer's introduction date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     * * @param errors The error map
     *
     * @param computer The computer to test
     */
    private void assertComputerIntroductionDateIsNullOrValid(Map<String, String> errors, ComputerDto computer) {
        if (computer.getIntroduced() != null && (computer.getIntroduced().toEpochDay() < 0 || computer.getIntroduced().isAfter(LocalDate.now()))) {
            addError(errors, COMPUTER_INTRODUCED_FIELD, "Introduction date must be empty or a valid timestamp !");
        }
    }

    /**
     * Assert the computer's discontinuation date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     * * @param errors The error map
     *
     * @param computer The computer to test
     */
    private void assertComputerDiscontinuedDateIsNullOrValid(Map<String, String> errors, ComputerDto computer) {
        if (computer.getDiscontinued() != null && (computer.getDiscontinued().toEpochDay() < 0 || computer.getDiscontinued().isAfter(LocalDate.now()))) {
            addError(errors, COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be empty or a valid timestamp !");
        }

        if ((computer.getIntroduced() == null && computer.getDiscontinued() != null) || (computer.getIntroduced() != null && computer.getDiscontinued() != null && computer.getIntroduced().isAfter(computer.getDiscontinued()))) {
            addError(errors, COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be superior to the introduction date !");
        }
    }

    /**
     * Assert the company id is null or valid, throws an {@link IllegalArgumentException} otherwise.
     * * @param errors The error map
     *
     * @param computer The computer to test
     */
    private void assertCompanyIsNullOrExist(Map<String, String> errors, ComputerDto computer) {
        if (computer.getCompanyId() != null && !companyService.get(computer.getCompanyId()).isPresent()) {
            addError(errors, COMPUTER_COMPANY_FIELD, "Company which introduced the computer does not exist");
        }
    }

    public enum Singleton {
        INSTANCE(new ComputerValidator(CompanyServiceImpl.getInstance()));

        Validator<ComputerDto> validator;

        /**
         * Constructor.
         *
         * @param validator The validator instance
         */
        Singleton(Validator<ComputerDto> validator) {
            this.validator = validator;
        }

        public Validator<ComputerDto> getValidator() {
            return validator;
        }
    }
}
