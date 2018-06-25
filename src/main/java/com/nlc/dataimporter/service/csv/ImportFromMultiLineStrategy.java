package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.repositories.CarInfoRepository;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImportFromMultiLineStrategy implements ImportFromTextStrategy {

    @Autowired CarInfoRepository carInfoRepository;

    private List<CarInfo> carsToImport = new ArrayList<>();

    @Override
    public void importData(String dataToImport) {
        populateCarsToImport(dataToImport);
        saveCars();
    }

    private void saveCars() {
        carsToImport.forEach(c -> {
            getRepository().save(c);
        });
    }

    protected CarInfoRepository getRepository() {
        return carInfoRepository;
    }

    private void populateCarsToImport(String dataToImport) {
        for (String line : dataToImport.split(System.lineSeparator())) {
            carsToImport.add(getSingleLineDataToCarInfoConverter().importFromSource(line));
        }
    }

    protected SingleLineDataToCarInfo getSingleLineDataToCarInfoConverter() {
        return new SingleLineDataToCarInfo();
    }

    @Override
    public Object getImportedData() {
        return this.carsToImport;
    }

}
