package com.example.ecommercesneakers.Exceptions;

public class BrandNotSupportedException extends RuntimeException{
    public BrandNotSupportedException(String message)
    {
        super(message);
    }
    public BrandNotSupportedException()
    {
        this("Brand not supported");
    }
}
