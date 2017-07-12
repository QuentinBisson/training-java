package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
import fr.ebiz.computerdatabase.persistence.factory.ConnectionPool;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.persistence.transaction.impl.TransactionManagerImpl;
import fr.ebiz.computerdatabase.service.CompanyService;

import java.util.List;
import java.util.Optional;

public class CompanyServiceImpl implements CompanyService {

    private static CompanyService instance;
    private final CompanyDao companyDao;
    private final ComputerDao computerDao;

    /**
     * Service constructor used to inject a {@link ConnectionPool} instance.
     *
     * @param companyDao The company dao
     * @param computerDao The computer dao to inject
     */
    private CompanyServiceImpl(CompanyDao companyDao, ComputerDao computerDao) {
        this.companyDao = companyDao;
        this.computerDao = computerDao;
    }

    /**
     * Get the service instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the service singleton instance
     */
    public static CompanyService getInstance() {
        if (instance == null) {
            synchronized (CompanyServiceImpl.class) {
                if (instance == null) {
                    instance = new CompanyServiceImpl(CompanyDaoImpl.getInstance(), ComputerDaoImpl.getInstance());
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
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        TransactionManager tx = TransactionManagerImpl.getInstance();
        tx.open(false);

        try {
            return companyDao.get(id);
        } finally {
            tx.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<Company> getAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (pageable.getElements() <= 0) {
            throw new IllegalArgumentException("The number of returned elements must be > 0");
        }

        TransactionManager tx = TransactionManagerImpl.getInstance();
        tx.open(true);

        try {
            int numberOfCompanies = companyDao.count();
            int totalPage = PagingUtils.countPages(pageable.getElements(), numberOfCompanies);

            if (pageable.getPage() < 0 || pageable.getPage() > totalPage - 1) {
                throw new IllegalArgumentException("Page number must be [0-" + (totalPage - 1) + "]");
            }
            List<Company> companies = companyDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements());

            return Page.builder()
                    .currentPage(pageable.getPage())
                    .totalPages(totalPage)
                    .totalElements(numberOfCompanies)
                    .elements(companies)
                    .build();
        } finally {
            tx.close();
        }
    }

    @Override
    public void delete(Company company) {
        TransactionManager tx = TransactionManagerImpl.getInstance();
        tx.open(true);

        try {
            computerDao.deleteByCompanyId(company.getId());
            companyDao.delete(company.getId());

            tx.commit();
        } finally {
            tx.close();
        }
    }
}
