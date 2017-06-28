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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return getId() == company.getId();
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
}
