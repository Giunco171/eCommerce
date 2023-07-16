package com.example.ecommercesneakers.Exceptions;

public class ProdottoNotFoundException extends RuntimeException{
    public ProdottoNotFoundException(String message)
    {
        super(message);
    }

    public ProdottoNotFoundException()
    {
        this("Prodotto not found");
    }
}
