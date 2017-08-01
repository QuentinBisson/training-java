package fr.ebiz.computerdatabase.service.validator.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.validator.Validator;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class ComputerValidator implements Validator<ComputerDto> {

    private static final String COMPUTER_NAME_FIELD = "name";
    private static final String COMPUTER_INTRODUCED_FIELD = "introduced";
    private static final String COMPUTER_DISCONTINUED_FIELD = "discontinued";
    private static final String COMPUTER_COMPANY_FIELD = "company";

    @Autowired
    private CompanyService companyService;

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
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertComputerNameIsFilled(Map<String, String> errors, ComputerDto computer) {
        if (computer.getName() == null || computer.getName().trim().isEmpty()) {
            errors.put(COMPUTER_NAME_FIELD, "Name should be filled !");
        }
    }

    /**
     * Assert the computer's introduction date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertComputerIntroductionDateIsNullOrValid(Map<String, String> errors, ComputerDto computer) {
        if (computer.getIntroduced() != null && (computer.getIntroduced().toEpochDay() < 0 || computer.getIntroduced().isAfter(LocalDate.now()))) {
            errors.put(COMPUTER_INTRODUCED_FIELD, "Introduction date must be empty or a valid timestamp !");
        }
    }

    /**
     * Assert the computer's discontinuation date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertComputerDiscontinuedDateIsNullOrValid(Map<String, String> errors, ComputerDto computer) {
        if (computer.getDiscontinued() != null && (computer.getDiscontinued().toEpochDay() < 0 || computer.getDiscontinued().isAfter(LocalDate.now()))) {
            errors.put(COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be empty or a valid timestamp !");
        }

        if ((computer.getIntroduced() == null && computer.getDiscontinued() != null) || (computer.getIntroduced() != null && computer.getDiscontinued() != null && computer.getIntroduced().isAfter(computer.getDiscontinued()))) {
            errors.put(COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be superior to the introduction date !");
        }
    }

    /**
     * Assert the company id is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param errors   The map of errors
     * @param computer The computer to test
     */
    private void assertCompanyIsNullOrExist(Map<String, String> errors, ComputerDto computer) {
        if (computer.getCompanyId() != null && !companyService.exists(computer.getCompanyId())) {
            errors.put(COMPUTER_COMPANY_FIELD, "Company which introduced the computer does not exist");
        }
    }


}
