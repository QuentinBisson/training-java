package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;

import java.util.List;
import java.util.Optional;

public interface ComputerService {

    /**
     * Get the computer with it's id.
     *
     * @param id The id of the computer to get
     * @return The computer if it exists or Optional.empty() if it does not
     */
    Optional<ComputerDto> get(int id);

    /**
     * Get the computers with pagination.
     *
     * @param request The filtering request
     * @return The paginated computers
     */
    Page<ComputerDto> getAll(GetAllComputersRequest request);

    /**
     * Assert a computer is valid and insert it if it is.
     *
     * @param computer The computer to insert
     * @return the new id
     */
    int insert(ComputerDto computer);

    /**
     * Assert a computer is valid and update it if it is.
     *
     * @param computer The computer to update
     */
    void update(ComputerDto computer);

    /**
     * Assert a computer is valid and delete it if it is.
     *
     * @param computerId The computer to delete
     */
    void delete(Integer computerId);

    /**
     * Delete computers introduced by a company.
     *
     * @param companyId The id of the company
     */
    void deleteByCompanyId(Integer companyId);

    /**
     * Delete computers introduced by a company.
     *
     * @param ids The ids of the computer to delete
     */
    void deleteComputers(List<Integer> ids);
}
