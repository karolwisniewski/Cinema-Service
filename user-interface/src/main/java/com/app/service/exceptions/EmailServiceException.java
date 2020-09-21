package com.app.service.exceptions;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message){
        super(message);
    }
}
