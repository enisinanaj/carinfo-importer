package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.repositories.CarInfoRepository;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportFromSingleLineStrategy implements ImportFromTextStrategy {

    @Autowired CarInfoRepository carInfoRepository;
    private CarInfo importedCarInfo;

    @Override
    public void importData(String dataToImport) {
        CarInfo carInfo = getSingleLineDataToCarInfoConverter().importFromSource(dataToImport);
        this.importedCarInfo = getCarInfoRepository().save(carInfo);
    }

    @Override
    public Object getImportedData() {
        return this.importedCarInfo;
    }

    protected SingleLineDataToCarInfo getSingleLineDataToCarInfoConverter() {
        return new SingleLineDataToCarInfo();
    }

    protected CarInfoRepository getCarInfoRepository() {
        return carInfoRepository;
    }
}
