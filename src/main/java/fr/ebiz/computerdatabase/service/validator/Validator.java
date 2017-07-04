package fr.ebiz.computerdatabase.service.validator;

public interface Validator<T> {

    /**
     * Validate the form object.
     *
     * @param model The model to validate
     */
    void validate(T model);
}
