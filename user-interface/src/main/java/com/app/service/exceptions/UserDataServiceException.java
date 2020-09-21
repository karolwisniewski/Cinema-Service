package com.app.service.exceptions;

public class UserDataServiceException extends RuntimeException {
    public UserDataServiceException(String message){
        super(message);
    }
}
