package com.example.ecommercesneakers.Exceptions;

public class BrandNotFoundException extends RuntimeException{
    public BrandNotFoundException(String message)
    {
        super(message);
    }

    public BrandNotFoundException()
    {
        this("Brand not found");
    }
}
