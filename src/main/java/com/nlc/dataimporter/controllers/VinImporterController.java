package com.nlc.dataimporter.controllers;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import com.nlc.dataimporter.service.ImportFromTextStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Created by sinanaj on 19/06/2018.
 */
// Not sure whether it should be
@RestController
@RequestMapping("/carinfo")
public class VinImporterController {

    private ImportFromTextStrategyFactory importFromTextStrategyFactory;

    @Autowired
    public VinImporterController(ImportFromTextStrategyFactory importFromTextStrategyFactory) {
        this.importFromTextStrategyFactory = importFromTextStrategyFactory;
    }

    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity saveNewCarInfo(@RequestBody String companyData) {
        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy();
        strategy.importData(companyData);
        CarInfo result = (CarInfo) strategy.getImportedData();

        URI uri = getCurrentContextPath().path("/carinfo").path("/{id}").buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    protected ServletUriComponentsBuilder getCurrentContextPath() {
        return ServletUriComponentsBuilder.fromCurrentContextPath();
    }
}
