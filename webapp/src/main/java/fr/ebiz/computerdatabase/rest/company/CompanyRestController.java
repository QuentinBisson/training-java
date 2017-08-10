package fr.ebiz.computerdatabase.rest.company;

import fr.ebiz.computerdatabase.core.Company;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyRestController {

    private CompanyService companyService;

    /**
     * Constructor.
     *
     * @param companyService The company service
     */
    @Autowired
    public CompanyRestController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * GET the companies.
     *
     * @param pageable The pageable parameters
     * @return A page of companies
     */
    @GetMapping
    public Page<Company> readCompanies(Pageable pageable) {
        return this.companyService.getAll(pageable);
    }

    /**
     * GET a company by it's id.
     *
     * @param companyId The id of the company to get
     * @return The company if it exists
     */
    @GetMapping(value = "/{companyId}")
    public ResponseEntity<Company> readCompany(@PathVariable int companyId) {
        return this.companyService.get(companyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE a company.
     *
     * @param companyId The id of the company to delete
     * @return The deleted company
     */
    @DeleteMapping(value = "/{companyId}")
    public ResponseEntity<Company> deleteCompany(@PathVariable int companyId) {
        return companyService.get(companyId).map(c -> {
            companyService.delete(companyId);
            return ResponseEntity.ok(c);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}