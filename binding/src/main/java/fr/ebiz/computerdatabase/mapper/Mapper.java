package fr.ebiz.computerdatabase.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<T, U> {

    /**
     * Transform an entity to a dto.
     *
     * @param entity The entity to transform
     * @return The dto
     */
    U toDto(T entity);

    /**
     * Transform a dto to an entity.
     *
     * @param dto The dto to transform
     * @return The entity
     */
    T toEntity(U dto);

    /**
     * Transform a list of entities to a list of dtos.
     *
     * @param entities The entities to transform in dtos
     * @return The dtos
     */
    default List<U> toDto(List<T> entities) {
        if (entities == null) {
            throw new NullPointerException();
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Transform a list of entities to a list of dtos.
     *
     * @param dtos The list of dtos to transform to entities
     * @return A list of unmapped element
     */
    default List<T> toEntity(List<U> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

}
