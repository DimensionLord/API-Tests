package com.karine.driver;

import com.karine.models.IModel;
import com.karine.models.MultiPartModel;
import com.karine.payload.IPayload;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ApiDriver {


    private ApiDriver() {

    }

    private static final ApiDriver instance = new ApiDriver();

    private final Set<Cookie> cookies = new HashSet<>();

    static ApiDriver getInstance() {
        return instance;
    }

    @Contract(" -> new")
    private @NotNull Cookies buildCookies() {
        if (this.cookies.isEmpty()) {
            return new Cookies();
        } else {
            Cookie[] temp = new Cookie[this.cookies.size()];
            this.cookies.toArray(temp);
            return new Cookies(temp);
        }
    }

    @Step("Добавление Cookie")
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    private RequestSpecification simple(@NotNull IPayload payload) {
        RequestSpecification request = RestAssured.given()
                .headers(payload.headers())
                .cookies(buildCookies())
                .redirects()
                .follow(true)
                .redirects()
                .allowCircular(true);
        IModel model = payload.model();
        return (model == null ? request : request.body(payload.model()));
    }

    private RequestSpecification multipart(IPayload payload) {
        RequestSpecification request = RestAssured.given()
                .headers(payload.headers())
                .cookies(buildCookies());
        IModel model = payload.model();
        if (!(model instanceof MultiPartModel)) {
            throw new RuntimeException("Bad model type");
        }
        for (Map.Entry<String, String> entry : ((MultiPartModel) model).getData().entrySet()) {
            request.multiPart(entry.getKey(), entry.getValue(), "form-data");
        }
        return request;
    }

    @Step("Выполняю '{payload}'")
    public Response call(@NotNull String path, @NotNull String method, @NotNull IPayload payload) {
        RequestSpecification request;
        if (method.equalsIgnoreCase("MULTI")) {
            request = multipart(payload);
            method="POST";
        } else {
            request = simple(payload);
        }

        Response response = request.request(Method.valueOf(method.toUpperCase()), path);
        for (
                Cookie cookie : response.detailedCookies()) {
            addCookie(cookie);
        }
        return response;
    }
}
