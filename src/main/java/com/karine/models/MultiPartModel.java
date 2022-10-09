package com.karine.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class MultiPartModel implements IModel {
    @Getter
    private final Map<String, String> data = new HashMap<>();

    public MultiPartModel with(String key, String value) {
        data.put(key, value);
        return this;
    }
}
