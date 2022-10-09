package com.karine.api.headers;

import com.karine.annotations.HeaderTitle;
import com.karine.annotations.ProvideHeaders;
import io.restassured.http.Header;

@ProvideHeaders
public final class HeaderBundle extends com.karine.models.HeaderBundle {
    @HeaderTitle("Chrome Agent")
    public Header userAgent = new Header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");

    @HeaderTitle("Accept VND+JSON")
    public Header acceptVndJson = new Header("Accept", "application/vnd.api+json");

    @HeaderTitle("Json ContentType")
    public Header jsonContentType = new Header("Content-Type", "application/json;charset=UTF-8");

    @HeaderTitle("Referer")
    public Header referer = new Header("Referer", "https://domrfbank.ru/mortgage/");

    @HeaderTitle("Multipart ContentType")
    public Header multipart = new Header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary6itQOiw0qNlwUCXE");
}
