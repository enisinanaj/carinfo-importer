package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileNameValidator implements DataValidator<MultipartFile> {

    private String filename;

    @Override
    public void validate(MultipartFile objectToValidate) {
        checkNullObject(objectToValidate);

        checkNullOriginalFilename(objectToValidate);
        this.filename = StringUtils.cleanPath(objectToValidate.getOriginalFilename());

        checkDirectoryTraversal();
    }

    private void checkNullOriginalFilename(MultipartFile objectToValidate) {
        if (objectToValidate.getOriginalFilename() == null) {
            throw new StorageException("Cannot store file with null filename");
        }
    }

    private void checkDirectoryTraversal() {
        // Check could be extended to more cases with a regexp to conform to all check for the vulnerability
        // Owasp Directory Traversal
        if (filename.contains("..")) {
            // This is a security check
            throw new StorageException("Cannot store file with relative path outside current directory " + filename);
        }
    }

    private void checkNullObject(MultipartFile objectToValidate) {
        if (objectToValidate == null) {
            throw new StorageException("Cannot store null object");
        }
    }
}
