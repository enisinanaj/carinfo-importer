package com.nlc.dataimporter.service;

import com.nlc.dataimporter.service.storage.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    void store(MultipartFile file) throws StorageException;

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void init();

    String loadFileContentAsString(String filename);
}
