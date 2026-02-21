package com.infrastructure.mapper;

import java.util.List;

public interface BaseMapper<E, D> {

    E toEntity(D dto);

    D toDto(E entity);

    List<D> toDtoList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);
}
