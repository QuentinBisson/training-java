package fr.ebiz.computerdatabase.service;


import fr.ebiz.computerdatabase.model.Company;

import java.util.List;

public interface CompanyService {

    Company get(int id);

    List<Company> getAll();

}
