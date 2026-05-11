package com.infrastructure.controller;

import com.infrastructure.domain.search.FilterRequest;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.GeneralExceptionType;
import com.infrastructure.mapper.BaseMapper;
import com.infrastructure.model.BaseEntity;
import com.infrastructure.service.BaseService;
import com.infrastructure.validator.NotEmpty;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

public abstract class BaseController<E, ID extends Serializable, D> {
    protected final BaseService<E, ID> service;
    protected final BaseMapper<E, D> mapper;

    public BaseController(BaseService<E, ID> service, BaseMapper<E, D> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(path = "/add")
    public void insert(@RequestBody D dto) throws Exception {
        E e = mapper.toEntity(dto);
        ((BaseEntity) e).setId(null);
        service.save(e);
    }

    @PutMapping(path =  "/edit")
    public void edit(@RequestBody D dto) throws Exception {
        E e = mapper.toEntity(dto);
        if (((BaseEntity) e).getId() == null)
            throw new BaseException(GeneralExceptionType.FIELD_NOT_VALID, new Object[]{"id"});
        service.save(e);
    }

    @DeleteMapping(path = "/remove/{id}")
    public void remove(@PathVariable @NotEmpty(fieldName = "id") ID id) throws Exception {
        service.deleteById(id);
    }

    @GetMapping(path = "/{id}")
    public D find(@PathVariable("id") ID id) {
        E e = service.findById(id);
        return mapper.toDto(e);
    }

    @GetMapping
    public List<D> findAll() {
        List<E> list = service.findAll();
        return mapper.toDtoList(list);
    }

    @PostMapping(path = "/search")
    public List<D> findAll(@RequestBody FilterRequest filter) {
        List<E> list = service.findAll(filter);
        return mapper.toDtoList(list);
    }
}