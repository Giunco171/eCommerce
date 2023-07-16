package com.example.ecommercesneakers.Exceptions;

public class PrezzoCambiatoException extends RuntimeException{
    public PrezzoCambiatoException(String message)
    {
        super(message);
    }
    public PrezzoCambiatoException()
    {
        this("Prezzo cambiato exception");
    }
}
