package fr.ebiz.computerdatabase.service.validator;

import fr.ebiz.computerdatabase.utils.StringUtils;

import java.util.Map;

public abstract class AbstractValidator<T> implements Validator<T> {


    /**
     * Add a validation error to the form object.
     *
     * @param field The field in error
     *      @param errors Map of errors
     * @param error The validation error
     */
    public void addError(Map<String, String> errors, String field, String error) {
        if (StringUtils.isBlank(field)) {
            throw new IllegalArgumentException("Field is blank");
        }

        if (StringUtils.isBlank(error)) {
            throw new IllegalArgumentException("Eror is blank");
        }

        errors.put(field, error);
    }

}
