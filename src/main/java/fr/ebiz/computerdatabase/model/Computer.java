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
     * .
     * Computer uuid
     */
    private Integer id;

    /**
     * .
     * Computer name
     */
    private String name;

    /**
     * .
     * Introduction date of the computer
     */
    private OffsetDateTime introduced;

    /**
     * .
     * Discontinuation date of the computer
     */
    private OffsetDateTime discontinued;

    /**
     * .
     * Company if the computer was created by
     */
    private Integer companyId;
    /**
     * .
     * Company name the computer was created by
     */
    private String companyName;

    /**
     * .
     * Create a Computer builder instance
     *
     * @return a new computer builder
     */
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

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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

    public static class ComputerBuilder {

        private final Computer computer;

        /**
         * Create a new computer object to build.
         */
        ComputerBuilder() {
            computer = new Computer();
        }

        /**
         * Set the computer id.
         *
         * @param id The new id to set
         * @return The builder instance
         */
        public ComputerBuilder id(Integer id) {
            computer.id = id;
            return this;
        }

        /**
         * Set the computer name.
         *
         * @param name The new name to set
         * @return The builder instance
         */
        public ComputerBuilder name(String name) {
            computer.name = name;
            return this;
        }

        /**
         * Set the computer introduction date.
         *
         * @param introduced The new introduction date to set
         * @return The builder instance
         */
        public ComputerBuilder introduced(OffsetDateTime introduced) {
            computer.introduced = introduced;
            return this;
        }

        /**
         * Set the computer discontinuation date.
         *
         * @param discontinued The new discontinued to set
         * @return The builder instance
         */
        public ComputerBuilder discontinued(OffsetDateTime discontinued) {
            computer.discontinued = discontinued;
            return this;
        }

        /**
         * Set the computer's company id.
         *
         * @param companyId The new company id to set
         * @return The builder instance
         */
        public ComputerBuilder companyId(Integer companyId) {
            computer.companyId = companyId;
            return this;
        }

        /**
         * Set the computer's company name.
         *
         * @param companyName The new company id to set
         * @return The builder instance
         */
        public ComputerBuilder companyName(String companyName) {
            computer.companyName = companyName;
            return this;
        }

        /**
         * .
         * Return the built instance of Computer
         *
         * @return the computer
         */
        public Computer build() {
            return computer;
        }
    }
}
