package com.karine.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.karine.data.IUseSessionData;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CalculatorRequestModel implements IUseSessionData {
    public CalculatorRequestModel() {
        super();
    }

    @JsonProperty("type")
    String type;

    @JsonProperty("attributes")
    Map<String, Object> attributes = new HashMap<>();

    public CalculatorRequestModel putFromData(String key) {
        this.getAttributes().put(key, sessionData().get(key));
        return this;
    }

    public <T> T get(String s) {
        return (T) this.getAttributes().get(s);
    }
}
