package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDaoImpl implements CompanyDao {

    private static final String READ_QUERY = "SELECT * from company order by name LIMIT ? OFFSET ?";
    private static final String READ_BY_ID_QUERY = "SELECT * from company where id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from company";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());
    private static final String TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION = "Too many results were found for id = ";

    private static CompanyDao instance;

    private final DaoFactory daoFactory;

    /**
     * Service constructor used to inject a {@link DaoFactory} instance.
     *
     * @param daoFactory The dao factory to inject
     */
    private CompanyDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Get the dao instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the dao singleton instance
     */
    public static synchronized CompanyDao getInstance() {
        if (instance == null) {
            synchronized (CompanyDaoImpl.class) {
                if (instance == null) {
                    instance = new CompanyDaoImpl(DaoFactory.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_BY_ID_QUERY)) {

            statement.setInt(FIRST_PARAMETER_INDEX, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Company> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }

                if (found.isEmpty()) {
                    return Optional.empty();
                } else if (found.size() > 1) {
                    String message = TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION + id;
                    LOGGER.error(message);
                    throw new DaoException(message);
                }
                return Optional.of(found.get(0));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> getAll(int elements, int offset) throws DaoException {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_QUERY)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            statement.setInt(parameterIndex++, elements);
            statement.setInt(parameterIndex, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Company> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }
                return found;
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() throws DaoException {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt(FIRST_PARAMETER_INDEX);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    /**
     * Map a {@link ResultSet} to a {@link Company} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private Company mapEntity(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            return Company.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString(NAME_COLUMN_NAME)).build();
        }

        return null;
    }

}