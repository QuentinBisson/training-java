package fr.ebiz.computerdatabase.service.validator;

import fr.ebiz.computerdatabase.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractValidator<T> implements Validator<T> {

    private Map<String, String> errors;

    /**
     * Constructor.
     */
    public AbstractValidator() {
        this.errors = new HashMap<>();
    }

    /**
     * Add a validation error to the form object.
     *
     * @param field The field in error
     * @param error The validation error
     */
    public void addError(String field, String error) {
        if (StringUtils.isBlank(field)) {
            throw new IllegalArgumentException("Field is blank");
        }

        if (StringUtils.isBlank(error)) {
            throw new IllegalArgumentException("Eror is blank");
        }

        errors.put(field, error);
    }

    /**
     * Is the form valid ?
     *
     * @return true if no error were found
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
