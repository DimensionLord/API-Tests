package com.karine.payload;

import com.karine.models.IModel;
import io.restassured.http.Headers;


public interface IPayload {
    IModel model();

    Headers headers();
}
