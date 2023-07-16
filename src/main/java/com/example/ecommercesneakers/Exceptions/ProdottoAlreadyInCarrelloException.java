package com.example.ecommercesneakers.Exceptions;

public class ProdottoAlreadyInCarrelloException extends RuntimeException{
    public ProdottoAlreadyInCarrelloException(String message)
    {
        super(message);
    }
    public ProdottoAlreadyInCarrelloException()
    {
        this("Prodotto already in carrello");
    }
}
