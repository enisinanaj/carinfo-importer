package com.nlc.dataimporter.service.storage;

import org.springframework.context.annotation.Configuration;

@Configuration("storage")
public class StorageProperties {

    private String location = "uploads";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
