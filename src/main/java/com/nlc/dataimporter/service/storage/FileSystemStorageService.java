package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import com.nlc.dataimporter.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final List<DataValidator> validators = new ArrayList<>();

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());

        validators.add(new FileNameValidator());
        validators.add(new FileSizeValidator());
    }

    @Override
    public void store(MultipartFile file) throws StorageException {
        validateFile(file);
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    private void validateFile(MultipartFile file) {
        getValidators().forEach(validator -> validator.validate(file));
    }

    protected List<DataValidator> getValidators() {
        return validators;
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String loadFileContentAsString(String filename) {
        try (InputStream is = loadAsResource(filename).getInputStream()) {
            try (BufferedReader reader = getBufferedReader(is)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException io) {
                throw new StorageException("Unable to read resource.");
            }
        } catch (IOException e) {
            throw new StorageException("Unable to read resource.");
        }
    }

    protected BufferedReader getBufferedReader(InputStream inputStream) throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf8")));
    }
}
