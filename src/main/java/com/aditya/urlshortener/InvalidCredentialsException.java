package com.aditya.urlshortener;

public class InvalidCredentialsException extends RuntimeException{


    public InvalidCredentialsException() {


        super("Invalid username or password");

    }
}
