package fr.ebiz.computerdatabase.persistence.exception;

public class DaoException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The exception message
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message The exception message
     * @param cause   The original cause
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause The original cause
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

}