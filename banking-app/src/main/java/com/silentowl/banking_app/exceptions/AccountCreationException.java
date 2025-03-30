package com.silentowl.banking_app.exceptions;

public class AccountCreationException extends RuntimeException{
    public AccountCreationException(Long customerId){
        super("Error creating account for customer. Customer id : " + customerId);
    }
}
