package com.example.ecommercesneakers.Exceptions;

public class EmailAlreadyUsedException extends RuntimeException{
    public EmailAlreadyUsedException(String message)
    {
        super(message);
    }
    public EmailAlreadyUsedException()
    {
        this("Email already used");
    }
}
