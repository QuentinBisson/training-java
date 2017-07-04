package fr.ebiz.computerdatabase.service.validator.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    /**
     * Constructor.
     *
     * @param errors The validation errors
     */
    public ValidationException(Map<String, String> errors) {
        super("Invalid form");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}