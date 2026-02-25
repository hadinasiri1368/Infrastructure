package com.infrastructure.service;

import com.infrastructure.repository.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)

public class BaseService<T, ID extends Serializable> {

    protected final BaseRepository<T, ID> repository;

    public BaseService(BaseRepository repository) {
        this.repository = repository;
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
}
