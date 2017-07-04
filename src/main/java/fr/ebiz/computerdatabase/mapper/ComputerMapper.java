package fr.ebiz.computerdatabase.mapper;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.model.Computer;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ComputerMapper implements Mapper<Computer, ComputerDto> {

    @Override
    public ComputerDto toDto(Computer entity) {
        return ComputerDto.builder()
                .id(entity.getId() == null ? 0 : entity.getId())
                .name(entity.getName())
                .introduced(entity.getIntroduced() != null ? entity.getIntroduced().toLocalDate() : null)
                .discontinued(entity.getDiscontinued() != null ? entity.getDiscontinued().toLocalDate() : null)
                .companyId(entity.getCompanyId())
                .companyName(entity.getCompanyName())
                .build();
    }

    @Override
    public Computer toEntity(ComputerDto dto) {
        return Computer.builder()
                .id(dto.getId() == null ? 0 : dto.getId())
                .name(dto.getName())
                .introduced(dto.getIntroduced() != null ? OffsetDateTime.of(dto.getIntroduced(), LocalTime.MIDNIGHT, ZoneOffset.UTC) : null)
                .discontinued(dto.getDiscontinued() != null ? OffsetDateTime.of(dto.getDiscontinued(), LocalTime.MIDNIGHT, ZoneOffset.UTC) : null)
                .companyId(dto.getCompanyId())
                .companyName(dto.getCompanyName())
                .build();
    }

}
