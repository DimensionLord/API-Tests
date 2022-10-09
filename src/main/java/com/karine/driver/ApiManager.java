package com.karine.driver;

import com.karine.annotations.ProvideHeaders;
import com.karine.models.HeaderBundle;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import lombok.SneakyThrows;
import org.atteo.classindex.ClassIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ApiManager {
    private final static ApiManager instance = new ApiManager();

    @SneakyThrows
    private ApiManager() {
        for (Class<?> headerClass : ClassIndex.getAnnotated(ProvideHeaders.class)) {
            HeaderBundle bundle = (HeaderBundle) headerClass.getConstructor().newInstance();
            headers.putAll(bundle.getHeaders());
        }
    }

    private final Map<String, Header> headers = new HashMap<>();

    public static ApiManager getInstance() {
        return instance;
    }

    public Set<Header> getHeaders(String @NotNull ... headers) {
        if (headers.length == 0) {
            return Collections.emptySet();
        }
        Set<Header> headerSet = new HashSet<>();
        for (String header : headers) {
            headerSet.add(this.headers.get(header));
        }
        return headerSet;
    }


}
