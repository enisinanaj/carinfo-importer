package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.service.DataValidator;

import java.util.regex.Pattern;

public class SingleLineRowValidator implements DataValidator<String> {

    private static final String CSV_ROW_PATTERN = "^[A-Z0-9]{17},[A-Z]{2},[A-Z0-1]{2},.*$";
    private final Pattern pattern = Pattern.compile(CSV_ROW_PATTERN);

    @Override
    public void validate(String objectToValidate) {
        if (!pattern.matcher(objectToValidate).matches()) {
            throw new RuntimeException("Bad request.");
        }
    }
}
