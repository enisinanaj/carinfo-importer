package com.nlc.dataimporter.service;

public interface DataValidator<T> {

    void validate(T objectToValidate);
}
