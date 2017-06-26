package fr.ebiz.computerdatabase.persistence.exception;

public class DaoConfigurationException extends DaoException {

    public DaoConfigurationException(String message) {
        super(message);
    }

    public DaoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoConfigurationException(Throwable cause) {
        super(cause);
    }

}