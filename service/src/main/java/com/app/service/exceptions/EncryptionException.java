package com.app.service.exceptions;

public class EncryptionException extends RuntimeException {
    public EncryptionException(String message){
        super(message);
    }
}
