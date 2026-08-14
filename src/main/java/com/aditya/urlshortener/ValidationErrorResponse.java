package com.aditya.urlshortener;

import java.util.List;

public class ValidationErrorResponse extends ErrorResponse{

    private List<FieldValidationError> details;

    public ValidationErrorResponse() {}

    public ValidationErrorResponse(int status, String error, String message, List<FieldValidationError> details) {
        super(status, error, message);   // pass to parent constructor
        this.details = details;
    }

    public List<FieldValidationError> getDetails() { return details; }
    public void setDetails(List<FieldValidationError> details) { this.details = details; }
}
