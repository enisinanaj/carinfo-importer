package com.nlc.dataimporter.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by sinanaj on 19/06/2018.
 */
@Entity
public class CarInfo {

    @Id
    @GeneratedValue
    private Long id;

    private String vin;
    private String input1;
    private String input2;
    private String carMake;

    public CarInfo() {
    }

    public CarInfo(String vin, String input1, String input2, String carMake) {
        this.vin = vin;
        this.input1 = input1;
        this.input2 = input2;
        this.carMake = carMake;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getInput1() {
        return input1;
    }

    public void setInput1(String input1) {
        this.input1 = input1;
    }

    public String getInput2() {
        return input2;
    }

    public void setInput2(String input2) {
        this.input2 = input2;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
