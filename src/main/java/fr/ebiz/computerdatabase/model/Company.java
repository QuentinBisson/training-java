package fr.ebiz.computerdatabase.model;

import java.util.Objects;

/**
 * Created by qbisson on 26/06/17.
 * <p>
 * Model class used to represent companies managed by this application
 */
public class Company {

    /**
     * Company uuid
     */
    private Integer id;
    /**
     * Company name
     */
    private String name;

    public Company() {
        super();
    }

    public static CompanyBuilder builder() {
        return new CompanyBuilder();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(getId(), company.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static class CompanyBuilder implements Builder<Company> {
        private Company company;

        public CompanyBuilder() {
            company = new Company();
        }

        public CompanyBuilder id(int id) {
            company.id = id;
            return this;
        }

        public CompanyBuilder name(String name) {
            company.name = name;
            return this;
        }

        @Override
        public Company build() {
            return company;
        }
    }
}
