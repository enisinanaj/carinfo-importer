package com.nlc.dataimporter.service.csv;


import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.service.DataValidator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sinanaj on 20/06/2018.
 */
public class SingleLineDataToCarInfo {

    private String source;
    private CarInfo importedCarInfo;
    private List<DataValidator> validators = new ArrayList<>();

    {
        validators.add(new SingleLineRowValidator());
    }

    public CarInfo importFromSource(String source) {
        validateSource(source);
        this.source = source;
        convertSourceToCarInfo();

        return importedCarInfo;
    }

    protected boolean validateSource(String source) {
        getValidators().forEach(v -> v.validate(source));

        return true;
    }

    protected List<DataValidator> getValidators() {
        return this.validators;
    }

    private void convertSourceToCarInfo() {
        String[] sourceColumns = source.split(",");
        CarInfo carInfo = createNewCarInfo();

        carInfo.setVin(sourceColumns[0]);
        carInfo.setInput1(sourceColumns[1]);
        carInfo.setInput2(sourceColumns[2]);
        carInfo.setCarMake(sourceColumns[3]);

        this.importedCarInfo = carInfo;
    }

    private CarInfo createNewCarInfo() {
        return new CarInfo();
    }
}
