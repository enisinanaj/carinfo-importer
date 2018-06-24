package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements DataValidator<MultipartFile> {

    @Override
    public void validate(MultipartFile objectToValidate) {
        if (objectToValidate.isEmpty()) {
            throw new StorageException("Failed to store empty file " + objectToValidate);
        }
    }
}
