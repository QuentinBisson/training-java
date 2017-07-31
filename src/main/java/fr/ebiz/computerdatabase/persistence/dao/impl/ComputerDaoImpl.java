package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
public class ComputerDaoImpl implements ComputerDao {

    private static final String ID_COLUMN_NAME = "id";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";
    private static final String COMPUTER_NAME = "computerName";
    private static final String COMPANY_NAME = "companyName";

    private static final String READ_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName FROM computer computer LEFT JOIN company company ON computer.company_id = company.id";
    private static final String READ_BY_ID_QUERY = "SELECT computer.id, computer.name AS computerName, computer.introduced, computer.discontinued, computer.company_id, company.name AS companyName FROM computer LEFT JOIN company company ON computer.company_id = company.id WHERE computer.id = :id";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from computer LEFT JOIN company company ON computer.company_id = company.id";
    private static final String INSERT_QUERY = "INSERT INTO computer(name, introduced, discontinued, company_id) VALUES (:computerName, :introduced, :discontinued, :company_id) ";
    private static final String UPDATE_QUERY = "UPDATE computer SET name = :computerName, introduced = :introduced, discontinued = :discontinued, company_id = :company_id WHERE id = :id";
    private static final String DELETE_QUERY = "DELETE FROM computer";
    private static final String DELETE_COMPUTERS_FOR_COMPANY_QUERY = "DELETE FROM computer WHERE company_id = :company_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Constructor.
     *
     * @param dataSource The JDBC DataSource
     */
    @Autowired
    public ComputerDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Map a {@link ResultSet} to a {@link Computer} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private static Computer mapRow(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer.ComputerBuilder builder = Computer.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString(COMPUTER_NAME))
                    .introduced(DaoUtils.toDate(resultSet.getTimestamp(INTRODUCED_COLUMN_NAME)))
                    .discontinued(DaoUtils.toDate(resultSet.getTimestamp(DISCONTINUED_COLUMN_NAME)));

            // Handle the possibility of having the company id as null
            Integer companyId = resultSet.getInt(COMPANY_ID_COLUMN_NAME);
            if (resultSet.wasNull()) {
                companyId = null;
            }
            builder.company(Company.builder()
                    .id(companyId)
                    .name(resultSet.getString(COMPANY_NAME))
                    .build());
            return builder.build();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Computer> get(int id) {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID_COLUMN_NAME, id);

        return Optional.ofNullable(this.jdbcTemplate.queryForObject(READ_BY_ID_QUERY, parameters, (rs, row) -> mapRow(rs)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Computer> getAll(String query, int pageSize, int offset, SortColumn column, SortOrder order) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("pageSize", pageSize);
        parameters.put("offset", offset);

        String stringQuery = filterByName(parameters, READ_QUERY, query);
        stringQuery += String.format(" ORDER BY %s %s LIMIT :pageSize OFFSET :offset ", column.getField(), order.name());
        return this.jdbcTemplate.query(stringQuery, parameters, (rs, row) -> mapRow(rs));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(String query) {
        Map<String, Object> parameters = new HashMap<>();
        String stringQuery = filterByName(parameters, COUNT_QUERY, query);

        return this.jdbcTemplate.queryForObject(stringQuery, parameters, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(final Computer computer) {
        Map<String, Object> parameters = new HashMap<>();
        mapParameters(parameters, computer);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbcTemplate.update(INSERT_QUERY, new MapSqlParameterSource(parameters), keyHolder);
        computer.setId(keyHolder.getKey().intValue());

        return affectedRows == 1;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(final Computer computer) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_COLUMN_NAME, computer.getId());
        mapParameters(parameters, computer);

        return jdbcTemplate.update(UPDATE_QUERY, parameters) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) {
        return deleteComputers(Collections.singletonList(id));
    }

    @Override
    public boolean deleteComputers(List<Integer> ids) {
        Map<String, Integer> parameters = new HashMap<>();
        StringBuilder query = new StringBuilder(DELETE_QUERY + " WHERE ID IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                query.append(",");
            }
            String parameter = "id" + i;
            parameters.put(parameter, ids.get(i));
            query.append(":").append(parameter);
        }
        query.append(")");
        return jdbcTemplate.update(query.toString(), parameters) > 1;
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public boolean deleteByCompanyId(int companyId) {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("company_id", companyId);

        return jdbcTemplate.update(DELETE_COMPUTERS_FOR_COMPANY_QUERY, parameters) > 0;
    }

    /**
     * Map the computer to query arguments.
     *
     * @param parameters The map of parametrs to fill
     * @param computer   The computer used to fill the map
     */
    private void mapParameters(Map<String, Object> parameters, Computer computer) {
        parameters.put(COMPUTER_NAME, computer.getName());
        parameters.put(INTRODUCED_COLUMN_NAME, DaoUtils.toTimestamp(computer.getIntroduced()));
        parameters.put(DISCONTINUED_COLUMN_NAME, DaoUtils.toTimestamp(computer.getDiscontinued()));
        parameters.put(COMPANY_ID_COLUMN_NAME, computer.getCompany() != null ? computer.getCompany().getId() : null);
    }

    /**
     * Fill parameters to filter by computer or company name.
     *
     * @param parameters  The map of named query parameters
     * @param filterQuery The filter query
     * @param query       The original string query
     * @return The altered string query
     */
    private String filterByName(Map<String, Object> parameters, String query, String filterQuery) {
        if (!StringUtils.isBlank(filterQuery)) {
            String likeParameter = "%" + filterQuery + "%";
            query += " WHERE computer.name like :computerName OR company.name like :companyName";
            parameters.put(COMPUTER_NAME, likeParameter);
            parameters.put(COMPANY_NAME, likeParameter);
        }
        return query;
    }

}
