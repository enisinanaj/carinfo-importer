package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileNameValidator implements DataValidator<MultipartFile> {

    @Override
    public void validate(MultipartFile objectToValidate) {
        String filename = StringUtils.cleanPath(objectToValidate.getOriginalFilename());

        // Check could be extended to more cases with a regexp to conform to all check for the vulnerability
        // Owasp Directory Traversal
        if (filename.contains("..")) {
            // This is a security check
            throw new StorageException("Cannot store file with relative path outside current directory " + filename);
        }
    }
}
