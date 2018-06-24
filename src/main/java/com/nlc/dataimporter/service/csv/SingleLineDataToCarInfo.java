package com.nlc.dataimporter.service.csv;


import com.nlc.dataimporter.model.CarInfo;

/**
 * Created by sinanaj on 20/06/2018.
 */
public class SingleLineDataToCarInfo {

    private String source;
    private CarInfo importedCarInfo;

    public CarInfo importFromSource(String source) {

        if (!isSourceFormatValid(source)) {
            return null;
        }

        this.source = source;
        convertSourceToCarInfo();

        return importedCarInfo;
    }

    private boolean isSourceFormatValid(String source) {
        getValidator().validate(source);

        return true;
    }

    protected SingleLineRowValidator getValidator() {
        return new SingleLineRowValidator();
    }

    private void convertSourceToCarInfo() {
        String[] sourceColumns = source.split(",");
        CarInfo carInfo = new CarInfo();

        if (sourceColumns.length < 4) {
            throw new RuntimeException(String.format("Invalid row. The data row (%s) does not contain all the required information.", source));
        }

        carInfo.setVin(sourceColumns[0]);
        carInfo.setInput1(sourceColumns[1]);
        carInfo.setInput2(sourceColumns[2]);
        carInfo.setCarMake(sourceColumns[3]);

        this.importedCarInfo = carInfo;
    }
}
