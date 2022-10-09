package com.karine.models;

import com.karine.annotations.HeaderTitle;
import io.restassured.http.Header;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class HeaderBundle {
    protected HeaderBundle() {

    }

    @SneakyThrows
    public Map<String, Header> getHeaders() {
        Map<String, Header> result = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(HeaderTitle.class) && field.getType().equals(Header.class)) {
                Header header = (Header) field.get(this);
                result.put(field.getAnnotation(HeaderTitle.class).value(), header);
            }
        }
        return result;
    }
}
