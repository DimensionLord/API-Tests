package com.karine.tests;

import com.karine.api.headers.HeaderBundle;
import com.karine.data.IUseSessionData;
import com.karine.driver.IUseApiDriver;
import com.karine.models.MultiPartModel;
import com.karine.payload.IPayload;
import com.karine.payload.IUsePayload;
import com.karine.utils.GeneratorUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.testng.annotations.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class CurrencyExchangeTest implements IUsePayload, IUseApiDriver, IUseSessionData {


    @Step("Инициализация сессии")
    final void initSession() {
        IPayload payload = payload("Получение PHPSESSID")
                .withHeaders("Chrome Agent", "Accept VND+JSON", "Referer");
        String content = getDriver().call("https://domrfbank.ru/mortgage/", "Get", payload)
                .body()
                .asString();
        Matcher matcher = Pattern.compile("bitrix_sessid: \"([a-f0-9]{32})\"").matcher(content);
        if (!matcher.find()) {
            Allure.getLifecycle().addAttachment("Response", "text", ".txt", content.getBytes());
            throw new AssertionError("Не удалось инициализировать сессию");
        }
        sessionData().put("sessionId", matcher.group(1));
    }

    @Step("Расчет курсов обмена")
    public void exchange(){
        String from = GeneratorUtils.generateCurrency();
        String to = GeneratorUtils.generateCurrency(from);
        String currency= String.valueOf(ThreadLocalRandom.current().nextInt(100000000));
        MultiPartModel model=new MultiPartModel()
                .with("codevalfrom", from)
                .with("codevalto", to)
                .with("amount", currency)
                .with("channel", "inet")
                .with("segment", "premium");
        IPayload payload=payload("Расчет стоимости обмена")
                .withHeaders("Multipart ContentType","Accept VND+JSON")
                .withModel(model);
        getDriver().call("https://domrfbank.ru/local/ajax/currency_conversion.php","MULTI",payload)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/exchange.schema.json"));
    }

    @SneakyThrows
    @Test
    public void test() {
        initSession();
        exchange();
    }
}
