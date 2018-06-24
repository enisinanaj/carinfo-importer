package com.nlc.dataimporter.service;

import com.nlc.dataimporter.service.csv.ImportFromSingleLineStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportFromTextStrategyFactory {

    @Autowired
    private ImportFromSingleLineStrategy importFromSingleLineStrategy;

    public ImportFromTextStrategy createStrategy() {
        return getImportFromSingleLineStrategy();
    }

    protected ImportFromTextStrategy getImportFromSingleLineStrategy() {
        return importFromSingleLineStrategy;
    }

}
