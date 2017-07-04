package fr.ebiz.computerdatabase.persistence.exception;

public class DaoConfigurationException extends DaoException {

    /**
     * Constructor.
     *
     * @param message The exception message
     */
    public DaoConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message The exception message
     * @param cause   The original cause
     */
    public DaoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause The original cause
     */
    public DaoConfigurationException(Throwable cause) {
        super(cause);
    }

}