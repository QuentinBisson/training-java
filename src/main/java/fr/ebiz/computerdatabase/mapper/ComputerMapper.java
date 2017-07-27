package fr.ebiz.computerdatabase.mapper;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ComputerMapper implements Mapper<Computer, ComputerDto> {

    /**
     * Private Contructor for singleton.
     */
    private ComputerMapper() {
        super();
    }

    public static ComputerMapper getInstance() {
        return Singleton.INSTANCE.getMapper();
    }

    @Override
    public ComputerDto toDto(Computer entity) {
        return ComputerDto.builder()
                .id(entity.getId() == null ? 0 : entity.getId())
                .name(entity.getName())
                .introduced(entity.getIntroduced() != null ? entity.getIntroduced().toLocalDate() : null)
                .discontinued(entity.getDiscontinued() != null ? entity.getDiscontinued().toLocalDate() : null)
                .companyId(entity.getCompany() != null ? entity.getCompany().getId() : null)
                .companyName(entity.getCompany() != null ? entity.getCompany().getName() : null)
                .build();
    }

    @Override
    public Computer toEntity(ComputerDto dto) {
        return Computer.builder()
                .id(dto.getId() == null ? 0 : dto.getId())
                .name(dto.getName())
                .introduced(dto.getIntroduced() != null ? OffsetDateTime.of(dto.getIntroduced(), LocalTime.MIDNIGHT, ZoneOffset.UTC) : null)
                .discontinued(dto.getDiscontinued() != null ? OffsetDateTime.of(dto.getDiscontinued(), LocalTime.MIDNIGHT, ZoneOffset.UTC) : null)
                .company(Company.builder().id(dto.getCompanyId()).name(dto.getCompanyName()).build())
                .build();
    }


    enum Singleton {
        INSTANCE(new ComputerMapper());

        private final ComputerMapper mapper;

        /**
         * Constructor for the singleton pattern.
         *
         * @param mapper The unique mapper
         */
        Singleton(ComputerMapper mapper) {
            this.mapper = mapper;
        }

        public ComputerMapper getMapper() {
            return mapper;
        }
    }
}
