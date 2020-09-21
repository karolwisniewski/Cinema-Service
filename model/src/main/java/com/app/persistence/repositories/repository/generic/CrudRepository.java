package com.app.persistence.repositories.repository.generic;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID>{
    Optional<T> add (T item);
    Optional<T> update(T item);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
