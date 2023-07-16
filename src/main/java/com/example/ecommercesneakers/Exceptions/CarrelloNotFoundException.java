package com.example.ecommercesneakers.Exceptions;

public class CarrelloNotFoundException extends RuntimeException{
    public CarrelloNotFoundException(String message)
    {
        super(message);
    }
    public CarrelloNotFoundException()
    {
        this("Carrello not found.");
    }
}
