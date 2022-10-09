package com.karine.payload;

import com.karine.models.IModel;

public interface IMountablePayload extends IPayload {
    IMountablePayload withHeaders(String... headerTitles);

    IMountablePayload withHeader(String title, String header);

    IMountablePayload withModel(IModel model);
}
