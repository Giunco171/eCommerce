package com.example.ecommercesneakers.Exceptions;

public class NotEnoughtProductsException extends RuntimeException{
    public NotEnoughtProductsException(String message)
    {
        super(message);
    }
    public NotEnoughtProductsException()
    {
        this("Not enought products in store");
    }
}
