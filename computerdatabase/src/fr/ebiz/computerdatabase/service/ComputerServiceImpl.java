package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComputerServiceImpl implements ComputerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerServiceImpl.class.getName());
    private CompanyDao companyDao;
    private ComputerDao computerDao;

    public ComputerServiceImpl() {
        this.companyDao = (CompanyDao) DaoFactory.getInstance().make(Company.class);
        this.computerDao = (ComputerDao) DaoFactory.getInstance().make(Computer.class);
    }

    @Override
    public Computer get(int id) {
        try {
            return computerDao.get(id).orElse(null);
        } catch (DaoException exception) {
            LOGGER.error("Could not get the computer with id = " + id);
        }
        return null;
    }

    @Override
    public List<Computer> getAll() {
        return computerDao.getAll();
    }

    @Override
    public Page<Computer> getAllComputersWithCompanies(int currentPage, int maxElements) {
        List<Computer> computers = getAll();
        List<Company> companies = companyDao.getAll();

        for (Computer computer : computers) {
            for (Company company : companies) {
                if (computer.getCompanyId() != null && company.getId().equals(computer.getCompanyId())) {
                    computer.setCompany(company);
                }
            }
        }

        Page<Computer> page = new Page<>();
        page.setTotalPages(computerDao.count() / maxElements + (computerDao.count() % maxElements > 0 ? 1 : 0));
        page.setCurrentPage(currentPage);
        page.getElements().addAll(computers.subList((currentPage - 1) * maxElements, currentPage * maxElements > computers.size() ? computers.size() : currentPage * maxElements));
        return page;
    }

    @Override
    public void insert(Computer computer) {
        computerDao.insert(computer);
    }

    @Override
    public void update(Computer computer) {
        computerDao.update(computer);
    }

    @Override
    public void delete(Computer computer) {
        computerDao.delete(computer.getId());
    }

}
