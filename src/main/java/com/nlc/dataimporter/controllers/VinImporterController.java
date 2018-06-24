package com.nlc.dataimporter.controllers;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import com.nlc.dataimporter.service.ImportFromTextStrategyFactory;
import com.nlc.dataimporter.service.SourceType;
import com.nlc.dataimporter.service.StorageService;
import com.nlc.dataimporter.service.storage.FileSystemStorageService;
import com.nlc.dataimporter.service.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.transform.Source;
import java.net.URI;

/**
 * Created by sinanaj on 19/06/2018.
 */
// Not sure whether it should be
@RestController
@RequestMapping("/carinfo")
public class VinImporterController {

    private ImportFromTextStrategyFactory importFromTextStrategyFactory;
    private StorageService fileSystemStorageService;

    @Autowired
    public VinImporterController(ImportFromTextStrategyFactory importFromTextStrategyFactory, StorageService fileSystemStorageService) {
        this.importFromTextStrategyFactory = importFromTextStrategyFactory;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity saveNewCarInfo(@RequestBody String companyData) {
        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy(SourceType.SINGLE_LINE);
        strategy.importData(companyData);
        CarInfo result = (CarInfo) strategy.getImportedData();

        URI uri = getCurrentContextPath().path("/carinfo").path("/{id}").buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity importCarsFromFile(@RequestParam("file") MultipartFile multipartFile) {

        getStorageService().store(multipartFile);
        String fileContent = getStorageService().loadFileContentAsString(multipartFile.getOriginalFilename());

        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy(SourceType.MULTI_LINE);
        strategy.importData(fileContent);

        URI uri = getCurrentContextPath().path("/carinfo").buildAndExpand().toUri();
        return ResponseEntity.created(uri).build();
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity exceptionHandler() {
        return null;
    }

    protected ServletUriComponentsBuilder getCurrentContextPath() {
        return ServletUriComponentsBuilder.fromCurrentContextPath();
    }

    protected StorageService getStorageService() {
        return fileSystemStorageService;
    }
}
