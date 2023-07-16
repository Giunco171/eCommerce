package com.example.ecommercesneakers.Exceptions;

public class ProdottoAlreadyExistsException extends RuntimeException{
    public ProdottoAlreadyExistsException(String message)
    {
        super(message);
    }

    public ProdottoAlreadyExistsException()
    {
        this("Prodotto already exists");
    }
}
