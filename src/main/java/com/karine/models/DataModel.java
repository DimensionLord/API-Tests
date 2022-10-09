package com.karine.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataModel implements IModel{

    public DataModel(){

    }
    public DataModel(CalculatorRequestModel data) {
        this.data = data;
    }

    @JsonProperty("data")
    public CalculatorRequestModel data;
}
