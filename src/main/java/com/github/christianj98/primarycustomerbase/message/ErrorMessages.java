package com.github.christianj98.primarycustomerbase.message;

/**
 * The enum stores error messages in the application
 */
public enum ErrorMessages {
    CUSTOMER_ALREADY_EXIST_ERROR("Customer with given first name %s and last name %s already exist"),
    ADDRESS_ALREADY_EXIST_ERROR("Address with given street %s and city %s already exist"),
    ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR("Address cannot be deleted because address is assigned to the customer");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
