package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class CompanyServiceImpl implements CompanyService {

    private CompanyDao companyDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceImpl.class.getName());
    public CompanyServiceImpl() {
        this.companyDao = (CompanyDao) DaoFactory.getInstance().make(Company.class);
    }

    @Override
    public Company get(int id) {
        try {
            return companyDao.get(id).orElse(null);
        } catch (DaoException exception) {
            LOGGER.error("Could not get the company with id = " + id);
        }
        return null;
    }

    @Override
    public List<Company> getAll() {
        try {
            return companyDao.getAll();
        } catch (DaoException exception) {
            LOGGER.error("Could not get the companies");
        }

        return Collections.emptyList();
    }

}
