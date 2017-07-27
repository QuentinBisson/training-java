package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private TransactionManager txManager;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private ComputerService computerService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        try {
            txManager.open(false);
            return companyDao.get(id);
        } finally {
            txManager.close();
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

        try {
            txManager.open(true);
            Integer numberOfCompanies = companyDao.count();

            List<Company> companies;
            Integer totalPage = PagingUtils.countPages(pageable.getElements(), numberOfCompanies);
            if (pageable.getPage() < 0 || pageable.getPage() > totalPage - 1) {
                throw new IllegalArgumentException("Page number must be [0-" + (totalPage - 1) + "]");
            }
            if (totalPage == 0) {
                companies = Collections.emptyList();
            } else {
                companies = companyDao.getAll(pageable.getElements(), pageable.getPage() * pageable.getElements());
            }

            txManager.commit();

            Page<Company> page = Page.builder()
                    .currentPage(pageable.getPage())
                    .totalPages(totalPage)
                    .totalElements(numberOfCompanies)
                    .elements(companies)
                    .build();

            return page;
        } finally {
            txManager.close();
        }
    }

    @Override
    public void delete(Company company) {


        try {
            txManager.open(true);
            computerService.deleteByCompanyId(company.getId());
            companyDao.delete(company.getId());
            txManager.commit();
        } finally {
            txManager.close();
        }
    }
}
