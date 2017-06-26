package fr.ebiz.computerdatabase.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by qbisson on 26/06/17.
 * <p>
 * Model class used to represent computers managed by this application
 * <p>
 * A computer must be identified by an id and a name
 */
public class Computer {

    /**
     * Company uuid
     */
    private int id;
    /**
     * Company name
     */
    private String name;

    /**
     * Introduction date of the computer
     */
    private LocalDate introduced;

    /**
     * Discontinuation date of the computer
     */
    private LocalDate discontinued;

    /**
     * Company the computer was created by
     */
    private int companyId;

    /**
     * Company the computer was created by
     */
    private Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIntroduced() {
        return introduced;
    }

    public void setIntroduced(LocalDate introduced) {
        this.introduced = introduced;
    }

    public LocalDate getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(LocalDate discontinued) {
        this.discontinued = discontinued;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer computer = (Computer) o;
        return getId() == computer.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
