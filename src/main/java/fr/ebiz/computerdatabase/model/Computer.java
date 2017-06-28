package fr.ebiz.computerdatabase.model;

import java.time.OffsetDateTime;
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
     * Computer uuid
     */
    private Integer id;
    /**
     * Computer name
     */
    private String name;

    /**
     * Introduction date of the computer
     */
    private OffsetDateTime introduced;

    /**
     * Discontinuation date of the computer
     */
    private OffsetDateTime discontinued;

    /**
     * Company the computer was created by
     */
    private Integer companyId;

    public Computer() {
        super();
    }

    public static ComputerBuilder builder() {
        return new ComputerBuilder();
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

    public OffsetDateTime getIntroduced() {
        return introduced;
    }

    public OffsetDateTime getDiscontinued() {
        return discontinued;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer computer = (Computer) o;
        return Objects.equals(getId(), computer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Computer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", introduced=" + introduced +
                ", discontinued=" + discontinued +
                ", companyId=" + companyId +
                '}';
    }

    public static class ComputerBuilder implements Builder<Computer> {

        private Computer computer;

        public ComputerBuilder() {
            computer = new Computer();
        }

        public ComputerBuilder id(int id) {
            computer.id = id;
            return this;
        }

        public ComputerBuilder name(String name) {
            computer.name = name;
            return this;
        }

        public ComputerBuilder introduced(OffsetDateTime introduced) {
            computer.introduced = introduced;
            return this;
        }

        public ComputerBuilder discontinued(OffsetDateTime discontinued) {
            computer.discontinued = discontinued;
            return this;
        }

        public ComputerBuilder companyId(Integer companyId) {
            computer.companyId = companyId;
            return this;
        }

        @Override
        public Computer build() {
            return computer;
        }
    }
}
