package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.AbstractDao;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDaoImpl extends AbstractDao<Company, Integer> implements CompanyDao {

    private static final String COMPANY_TABLE_NAME = "company";

    public CompanyDaoImpl(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected String getTableName() {
        return COMPANY_TABLE_NAME;
    }

    @Override
    protected String getIDSelector() {
        return Columns.id.name() + EQUAL_OPERATOR + ":id";
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_INTO_QUERY + getTableName() + "(name) VALUES (:" + Columns.name.name() + ")";
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY + getTableName() + " SET name = :" + Columns.name.name() + WHERE_OPERATOR + getIDSelector();
    }

    @Override
    public Company map(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Company company = new Company();

            company.setId(resultSet.getInt(Columns.id.name()));
            company.setName(resultSet.getString(Columns.name.name()));
            return company;
        }

        return null;
    }

    @Override
    public void bindModel(PreparedStatement statement, Company model) throws SQLException {
        statement.setString(Columns.name.getIndex(), model.getName());
        bindID(statement, model.getId());
    }

    @Override
    public void bindID(PreparedStatement statement, Integer id) throws SQLException {
        statement.setInt(Columns.id.getIndex(), id);
    }

    private enum Columns {
        id, name;

        public int getIndex() {
            return ordinal() + 1;
        }
    }
}