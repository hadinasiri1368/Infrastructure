package com.infrastructure.controller;

import com.infrastructure.Permission;
import com.infrastructure.constants.Consts;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.GeneralExceptionType;
import com.infrastructure.mapper.BaseMapper;
import com.infrastructure.model.BaseEntity;
import com.infrastructure.service.BaseService;
import com.infrastructure.validator.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@RestController
@Validated
@RequestMapping(Consts.DEFAULT_PREFIX_API_URL)
@AllArgsConstructor
public abstract class BaseController<E, ID extends Serializable, D> {
    protected final BaseService<E, ID> service;
    protected final BaseMapper<E, D> mapper;

    @PostMapping(path = Consts.DEFAULT_VERSION_API_URL + "/add")
    public void insert(@RequestBody D dto) throws Exception {
        E e = mapper.toEntity(dto);
        ((BaseEntity) e).setId(null);
        service.save(e);
    }

    @PutMapping(path = Consts.DEFAULT_VERSION_API_URL + "/edit")
    public void edit(@RequestBody D dto) throws Exception {
        E e = mapper.toEntity(dto);
        if (((BaseEntity) e).getId() == null)
            throw new BaseException(GeneralExceptionType.FIELD_NOT_VALID, new Object[]{"id"});
        service.save(e);
    }

    @DeleteMapping(path = Consts.DEFAULT_VERSION_API_URL + "/remove/{id}")
    public void remove(@PathVariable @NotEmpty(fieldName = "id") ID id) throws Exception {
        E e = service.findById(id);
        if (e == null)
            throw new BaseException(GeneralExceptionType.FIELD_NOT_VALID, new Object[]{"id"});
        service.deleteById(id);
    }

    @GetMapping(path = Consts.DEFAULT_VERSION_API_URL + "/{id}")
    public E get(@PathVariable("id") ID id) {
        return service.findById(id);
    }

    @GetMapping
    public List<E> getAll() {
        return service.findAll();
    }
}