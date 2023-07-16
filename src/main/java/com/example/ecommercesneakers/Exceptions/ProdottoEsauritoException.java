package com.example.ecommercesneakers.Exceptions;

public class ProdottoEsauritoException extends RuntimeException{
    public ProdottoEsauritoException(String message)
    {
        super(message);
    }
    public ProdottoEsauritoException()
    {
        this("Prodotto esaurito");
    }
}
