package fr.ebiz.computerdatabase.service;


import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.impl.paging.Page;
import fr.ebiz.computerdatabase.service.impl.paging.Pageable;

import java.util.Optional;

public interface CompanyService {

    Optional<Company> get(int id);

    Page<Company> getAll(Pageable pageable);

}
