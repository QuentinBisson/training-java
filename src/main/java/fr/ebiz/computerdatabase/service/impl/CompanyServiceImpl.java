package fr.ebiz.computerdatabase.service.impl

import fr.ebiz.computerdatabase.dto.paging.Page
import fr.ebiz.computerdatabase.dto.paging.Pageable
import fr.ebiz.computerdatabase.dto.paging.PagingUtils
import fr.ebiz.computerdatabase.model.Company
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao
import fr.ebiz.computerdatabase.service.CompanyService
import fr.ebiz.computerdatabase.service.ComputerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Collections
import java.util.Optional

@Transactional(readOnly = true)
@Service
class CompanyServiceImpl
/**
 * Constructor.
 * @param companyDao
 * * The company DAO
 * *
 * @param computerService
 * * The computer service
 */
@Autowired
constructor(private val companyDao: CompanyDao, private val computerService: ComputerService) : CompanyService {

    /**
     * {@inheritDoc}
     */
    override fun get(id: Int): Optional<Company> {
        assertCompanyIdIsGreaterThanZero(id)
        return companyDao.get(id)
    }

    /**
     * {@inheritDoc}
     */
    override fun exists(id: Int): Boolean {
        assertCompanyIdIsGreaterThanZero(id)
        return companyDao.get(id).isPresent
    }

    /**
     * {@inheritDoc}
     */
    override fun getAll(pageable: Pageable?): Page<Company> {
        if (pageable == null) {
            throw IllegalArgumentException("Pagination object is null")
        }

        if (pageable.pageSize <= 0) {
            throw IllegalArgumentException("The number of returned elements must be > 0")
        }

        val numberOfCompanies = companyDao.count()

        val companies: List<Company>
        val totalPage = PagingUtils.countPages(pageable.pageSize, numberOfCompanies)
        if (pageable.page < 0 || pageable.page > totalPage!! - 1) {
            throw IllegalArgumentException("Page number must be [0-" + (totalPage!! - 1) + "]")
        }

        if (totalPage === 0) {
            companies = emptyList<Company>()
        } else {
            companies = companyDao.getAll(pageable.pageSize, pageable.page * pageable.pageSize)
        }

        return Page.builder<Any>()
                .currentPage(pageable.page)
                .totalPages(totalPage)
                .totalElements(numberOfCompanies)
                .elements(companies)
                .build()
    }

    @Transactional
    override fun delete(companyId: Int) {
        computerService.deleteByCompanyId(companyId)
        companyDao.delete(companyId)
    }

    /**
     * Assert the id is greater than 0.

     * @param id The id to check
     */
    private fun assertCompanyIdIsGreaterThanZero(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException("ID must be > 0")
        }
    }


}
