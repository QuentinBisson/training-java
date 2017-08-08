package fr.ebiz.computerdatabase.core;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by qbisson on 26/06/17.
 * <p>
 * Model class used to represent companies managed by this application
 */
@Entity(name = "company")
public class Company implements Serializable {

    /**
     * Company uuid.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Company name.
     */
    private String name;

    /**
     * Create a Company builder instance.
     *
     * @return a new company builder
     */
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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

    public static class CompanyBuilder {
        private final Company company;

        /**
         * .
         * Create a new company object to build;
         */
        CompanyBuilder() {
            company = new Company();
        }

        /**
         * .
         * Set the company id
         *
         * @param id The new id to set
         * @return The builder instance
         */
        public CompanyBuilder id(Integer id) {
            company.id = id;
            return this;
        }

        /**int
         * .
         * Set the company name
         *
         * @param name The new name to set
         * @return The builder instance
         */
        public CompanyBuilder name(String name) {
            company.name = name;
            return this;
        }

        /**
         * .
         * Return the built instance of Company
         *
         * @return the company
         */
        public Company build() {
            return company;
        }
    }
}
