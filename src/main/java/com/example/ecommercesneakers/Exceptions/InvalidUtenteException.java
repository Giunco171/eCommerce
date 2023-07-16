package com.example.ecommercesneakers.Exceptions;

public class InvalidUtenteException extends RuntimeException{
    public InvalidUtenteException(String message)
    {
        super(message);
    }
    public InvalidUtenteException()
    {
        this("Invalid Utente");
    }
}
