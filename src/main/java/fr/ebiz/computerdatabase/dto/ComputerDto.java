package fr.ebiz.computerdatabase.dto;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by qbisson on 26/06/17.
 * <p>
 * Model class used to represent computers managed by this application
 * <p>
 * A computer must be identified by an id and a name
 */
public class ComputerDto {

    /**
     * Computer uuid.
     */
    private Integer id;

    /**
     * Computer name.
     */
    private String name;

    /**
     * Introduction date of the computer.
     */
    private LocalDate introduced;

    /**
     * Discontinuation date of the computer.
     */
    private LocalDate discontinued;
    /**
     * Company id.
     */
    private Integer companyId;
    /**
     * Company name.
     */
    private String companyName;

    /**
     * .
     * Create a ComputerDto builder instance
     *
     * @return a new computer builder
     */
    public static ComputerDto.ComputerDtoBuilder builder() {
        return new ComputerDto.ComputerDtoBuilder();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getIntroduced() {
        return introduced;
    }

    public LocalDate getDiscontinued() {
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
        ComputerDto that = (ComputerDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static class ComputerDtoBuilder {

        private final ComputerDto computer;

        /**
         * .
         * Create a new computer object to build;
         */
        ComputerDtoBuilder() {
            computer = new ComputerDto();
        }

        /**
         * .
         * Set the computer id
         *
         * @param id The new id to set
         * @return The builder instance
         */
        public ComputerDtoBuilder id(int id) {
            computer.id = id;
            return this;
        }

        /**
         * .
         * Set the computer name
         *
         * @param name The new name to set
         * @return The builder instance
         */
        public ComputerDtoBuilder name(String name) {
            computer.name = name;
            return this;
        }

        /**
         * .
         * Set the computer introduction date
         *
         * @param introduced The new introduction date to set
         * @return The builder instance
         */
        public ComputerDtoBuilder introduced(LocalDate introduced) {
            computer.introduced = introduced;
            return this;
        }

        /**
         * .
         * Set the computer discontinuation date
         *
         * @param discontinued The new discontinued to set
         * @return The builder instance
         */
        public ComputerDtoBuilder discontinued(LocalDate discontinued) {
            computer.discontinued = discontinued;
            return this;
        }

        /**
         * .
         * Set the computer company id
         *
         * @param companyId The new company id to set
         * @return The builder instance
         */
        public ComputerDtoBuilder companyId(Integer companyId) {
            computer.companyId = companyId;
            return this;
        }


        /**
         * .
         * Set the computer company name
         *
         * @param companyName The new company name to set
         * @return The builder instance
         */
        public ComputerDtoBuilder companyName(String companyName) {
            computer.companyName = companyName;
            return this;
        }

        /**
         * .
         * Return the built instance of ComputerDto
         *
         * @return the computer
         */
        public ComputerDto build() {
            return computer;
        }
    }
}
