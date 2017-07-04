package fr.ebiz.computerdatabase.service.validator.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.service.validator.AbstractValidator;
import fr.ebiz.computerdatabase.service.validator.exception.ValidationException;

import java.time.LocalDate;

public class ComputerValidator extends AbstractValidator<ComputerDto> {

    public static final String COMPUTER_ID_FIELD = "id";
    public static final String COMPUTER_NAME_FIELD = "name";
    public static final String COMPUTER_INTRODUCED_FIELD = "introduced";
    public static final String COMPUTER_DISCONTINUED_FIELD = "discontinued";
    public static final String COMPUTER_COMPANY_FIELD = "company";

    private CompanyDao companyDao;

    /**
     * Constructor.
     *
     * @param companyDao The injected company dao
     */
    public ComputerValidator(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public void validate(ComputerDto computer) {
        assertComputerNameIsFilled(computer);
        assertComputerIntroductionDateIsNullOrValid(computer);
        assertComputerDiscontinuedDateIsNullOrValid(computer);
        assertCompanyIsNullOrExist(computer);

        if (!isValid()) {
            throw new ValidationException(getErrors());
        }
    }

    /**
     * Assert the computer's name is filled and not blank, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerNameIsFilled(ComputerDto computer) {
        if (computer.getName() == null || computer.getName().trim().isEmpty()) {
            addError(COMPUTER_NAME_FIELD, "Name should be filled !");
        }
    }

    /**
     * Assert the computer's introduction date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerIntroductionDateIsNullOrValid(ComputerDto computer) {
        if (computer.getIntroduced() != null && (computer.getIntroduced().toEpochDay() < 0 || computer.getIntroduced().isAfter(LocalDate.now()))) {
            addError(COMPUTER_INTRODUCED_FIELD, "Introduction date must be empty or a valid timestamp !");
        }
    }

    /**
     * Assert the computer's discontinuation date is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerDiscontinuedDateIsNullOrValid(ComputerDto computer) {
        if (computer.getDiscontinued() != null && (computer.getDiscontinued().toEpochDay() < 0 || computer.getDiscontinued().isAfter(LocalDate.now()))) {
            addError(COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be empty or a valid timestamp !");
        }

        if ((computer.getIntroduced() == null && computer.getDiscontinued() != null) || (computer.getIntroduced() != null && computer.getDiscontinued() != null && computer.getIntroduced().isAfter(computer.getDiscontinued()))) {
            addError(COMPUTER_DISCONTINUED_FIELD, "Discontinuation date must be superior to the introduction date !");
        }
    }

    /**
     * Assert the company id is null or valid, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertCompanyIsNullOrExist(ComputerDto computer) {
        if (computer.getCompanyId() != null && !companyDao.get(computer.getCompanyId()).isPresent()) {
            addError(COMPUTER_COMPANY_FIELD, "Company which introduced the computer does not exist");
        }
    }


}
