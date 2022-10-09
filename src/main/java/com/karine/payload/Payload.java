package com.karine.payload;

import com.karine.driver.ApiManager;
import com.karine.models.IModel;
import io.restassured.http.Header;
import io.restassured.http.Headers;

import java.util.HashSet;
import java.util.Set;

public final class Payload implements IMountablePayload {

    Payload(String title) {
        this.title = title;
    }

    private IModel model;
    private Set<Header> headers = new HashSet<>();

    private final String title;

    @Override
    public IModel model() {
        return model;
    }

    @Override
    public Headers headers() {
        Header[] headers = new Header[this.headers.size()];
        return new Headers(this.headers.toArray(headers));
    }

    @Override
    public IMountablePayload withHeaders(String... headerTitles) {
        Set<Header> headerSet = ApiManager.getInstance().getHeaders(headerTitles);
        this.headers.addAll(headerSet);
        return this;
    }

    @Override
    public IMountablePayload withHeader(String title, String header) {
        this.headers.add(new Header(title, header));
        return this;
    }

    @Override
    public IMountablePayload withModel(IModel model) {
        this.model = model;
        return this;
    }

    @Override
    public String toString() {
        return title;
    }
}
