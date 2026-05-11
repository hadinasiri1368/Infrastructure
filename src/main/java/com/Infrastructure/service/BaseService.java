package com.infrastructure.service;

import com.infrastructure.domain.search.DynamicSpecification;
import com.infrastructure.domain.search.FilterField;
import com.infrastructure.domain.search.FilterRequest;
import com.infrastructure.domain.search.FilterValueConverter;
import com.infrastructure.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class BaseService<T, ID extends Serializable> {

    protected final BaseRepository<T, ID> repository;
    private final FilterValueConverter filterValueConverter;
    private final Class<T> entityClass;

    public BaseService(BaseRepository repository
            , FilterValueConverter filterValueConverter
            , Class<T> entityClass) {
        this.repository = repository;
        this.filterValueConverter = filterValueConverter;
        this.entityClass = entityClass;
    }


    public List<T> findAll() {
        return (List<T>) repository.findAll();
    }

    public T findById(ID id) {
        Optional<T> t = repository.findById(id);
        if (t.isPresent())
            return t.get();
        return null;
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }


    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void delete(T entity) {
        repository.delete(entity);
    }

    public List<T> findAll(FilterRequest filterRequest) {
        List<FilterField> convertedFilters = filterValueConverter.convertFilterValues(filterRequest.getFilters(), entityClass);
        DynamicSpecification<T> specification = new DynamicSpecification<>(convertedFilters);
        return repository.findAll(specification);
    }


    public Page<T> findAllByFilter(FilterRequest filterRequest) {
        List<FilterField> convertedFilters = filterValueConverter.convertFilterValues(filterRequest.getFilters(), entityClass);
        DynamicSpecification<T> specification = new DynamicSpecification<>(convertedFilters);
        return repository.findAll(specification
                , PageRequest.of(filterRequest.getPageNumber() != null ? filterRequest.getPageNumber() : 0
                , filterRequest.getPageSize() != null ? filterRequest.getPageSize() : 10));
    }

    public Page<T> findAllByFilter(FilterRequest filterRequest, Sort sort) {
        List<FilterField> convertedFilters = filterValueConverter.convertFilterValues(filterRequest.getFilters(), entityClass);
        DynamicSpecification<T> specification = new DynamicSpecification<>(convertedFilters);
        Pageable pageableWithSort = PageRequest.of(
                filterRequest.getPageNumber() != null ? filterRequest.getPageNumber() : 0,
                filterRequest.getPageSize() != null ? filterRequest.getPageSize() : 10,
                sort);
        return repository.findAll(specification, pageableWithSort);
    }
}
