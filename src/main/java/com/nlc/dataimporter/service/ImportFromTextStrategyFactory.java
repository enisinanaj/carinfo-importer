package com.nlc.dataimporter.service;

import com.nlc.dataimporter.service.csv.ImportFromSingleLineStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ImportFromTextStrategyFactory {

    @Autowired @Qualifier("importFromSingleLineStrategy") ImportFromTextStrategy importFromSingleLineStrategy;
    @Autowired @Qualifier("importFromMultiLineStrategy") ImportFromTextStrategy importFromMultiLineStrategy;

    public ImportFromTextStrategy createStrategy(SourceType sourceType) {
        switch (sourceType) {
            case MULTI_LINE:
                return getImportFromMultiLineStrategy();
            case SINGLE_LINE:
            default:
                return getImportFromSingleLineStrategy();

        }
    }

    protected ImportFromTextStrategy getImportFromMultiLineStrategy() {
        return importFromMultiLineStrategy;
    }

    protected ImportFromTextStrategy getImportFromSingleLineStrategy() {
        return importFromSingleLineStrategy;
    }

}
