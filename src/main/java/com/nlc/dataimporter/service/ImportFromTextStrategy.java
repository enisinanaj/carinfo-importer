package com.nlc.dataimporter.service;

public interface ImportFromTextStrategy {

    void importData(String dataToImport);
    Object getImportedData();
}
