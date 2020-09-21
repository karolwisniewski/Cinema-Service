package com.app.persistence.repositories.exception;

public class AbstractCrudRepositoryException extends RuntimeException {
    public AbstractCrudRepositoryException(String message ){
        super(message);
    }
}
