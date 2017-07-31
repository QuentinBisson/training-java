package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CompanyDaoImpl implements CompanyDao {

    private static final String READ_QUERY = "SELECT * FROM company ORDER BY name LIMIT :pageSize OFFSET :offset";
    private static final String READ_BY_ID_QUERY = "SELECT * FROM company WHERE id = :id";
    private static final String DELETE_QUERY = "DELETE FROM company WHERE id = :id";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM company";

    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Constructor.
     *
     * @param dataSource The dataSource
     */
    @Autowired
    public CompanyDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID_COLUMN_NAME, id);

        return Optional.ofNullable(this.jdbcTemplate.queryForObject(READ_BY_ID_QUERY, parameters, (rs, row) -> mapRow(rs)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> getAll(int pageSize, int offset) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("pageSize", pageSize);
        parameters.put("offset", offset);

        return this.jdbcTemplate.query(READ_QUERY, parameters, (rs, row) -> mapRow(rs));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return this.jdbcTemplate.queryForObject(COUNT_QUERY, Collections.emptyMap(), Integer.class);
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID_COLUMN_NAME, id);

        return jdbcTemplate.update(DELETE_QUERY, parameters) == 1;
    }

    /**
     * Map a {@link ResultSet} to a {@link Company} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private Company mapRow(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            return Company.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString(NAME_COLUMN_NAME)).build();
        }

        return null;
    }

}