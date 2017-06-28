package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;
import fr.ebiz.computerdatabase.service.impl.paging.PagingUtils;

import java.util.List;
import java.util.Optional;

public class CompanyServiceImpl implements CompanyService {

    private static CompanyService instance;
    private CompanyDao companyDao;

    private CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public synchronized static CompanyService getInstance() {
        if (instance == null) {
            instance = new CompanyServiceImpl(CompanyDaoImpl.getInstance());
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
        return companyDao.get(id);
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

        int numberOfCompanies = companyDao.count();
        int totalPage = PagingUtils.countPages(pageable.getElements(), numberOfCompanies);

        if (pageable.getPage() < 0 || pageable.getPage() > totalPage - 1) {
            throw new IllegalArgumentException("Page number must be [0-" + (totalPage - 1) + "]");
        }

        List<Company> companies = companyDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements());

        return Page.builder()
                .currentPage(pageable.getPage())
                .totalPages(totalPage)
                .elements(companies)
                .build();
    }

}
