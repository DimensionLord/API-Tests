package com.karine.tests;

import com.karine.data.IUseSessionData;
import com.karine.driver.IUseApiDriver;
import com.karine.models.CalculatorRequestModel;
import com.karine.models.DataModel;
import com.karine.models.IModel;
import com.karine.payload.IPayload;
import com.karine.payload.IUsePayload;
import com.karine.utils.GeneratorUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class CalculatorTest implements IUsePayload, IUseApiDriver, IUseSessionData {

    private final static String parametersPath = "data.get(%d).attributes.product.parameters";
    private final static String percentPath = "data.get(%d).attributes.product.interestAmount.get(0).interestPercent";

    @BeforeTest
    public void beforeTest() {
        sessionData().clear();
    }

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

    @Step("Подготовка данных для расчета")
    final void prepareData() {
        IPayload payload = payload("Ипотечные программы")
                .withHeaders("Chrome Agent", "Accept VND+JSON", "Referer", "Multipart ContentType");
        Response response = getDriver().call("https://domrfbank.ru/rest/v1/mortgage/products?sort=code&where[code]=product_preferential,product_family,it_specialist,product_building,product_ready,product_refinancing", "Get", payload);
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"))
                .statusCode(200);
        List<?> list = response.jsonPath().getJsonObject("data");
        int index = ThreadLocalRandom.current().nextInt(list.size());
        Map<String, ?> parameters = response.jsonPath()
                .getJsonObject(String.format(parametersPath, index));
        float rate = response.jsonPath()
                .getJsonObject(String.format(percentPath, index));
        GeneratorUtils.generateRandom(
                (Integer) parameters.get("minCreditAmount"),
                (Integer) parameters.get("maxCreditAmount"),
                "creditAmount");
        GeneratorUtils.generateRandom(
                (Integer) parameters.get("minCreditCalcPeriod"),
                (Integer) parameters.get("maxCreditCalcPeriod"),
                "creditPeriod");
        GeneratorUtils.generateRandom(
                (Integer) parameters.get("minDebtToPledge"),
                (Integer) parameters.get("maxDebtToPledge"),
                "initialPayment");
        sessionData().put("interestPercent", rate);
    }

    private void housing() {
        Integer percent = sessionData().<Integer>get("initialPayment");
        Integer creditAmount = sessionData().<Integer>get("creditAmount");
        BigDecimal creditPercent = new BigDecimal(100 - percent);
        int housing = new BigDecimal(creditAmount).divide(creditPercent, RoundingMode.UP)
                .multiply(new BigDecimal(100))
                .intValue();
        sessionData().put("housing", housing);
    }

    @Step("Запрос ипотечных условий")
    final void calculate() {
        Integer creditAmount = sessionData().<Integer>get("creditAmount");
        int housingValue = sessionData().get("housing");
        CalculatorRequestModel calculatorRequestModel = new CalculatorRequestModel();
        calculatorRequestModel.putFromData("interestPercent")
                .putFromData("creditPeriod");
        calculatorRequestModel.getAttributes().put("housingValue", housingValue);
        calculatorRequestModel.getAttributes().put("initialPayment", housingValue - creditAmount);
        calculatorRequestModel.setType("creditTermsByHousingCost");
        IModel model = new DataModel(calculatorRequestModel);
        IPayload payload = payload("Расчет ипотеки")
                .withHeaders("Chrome Agent", "Accept VND+JSON", "Json ContentType", "Referer")
                .withModel(model);
        model = getDriver().call("https://domrfbank.ru/rest/v1/mortgage/calculator/creditTermsByHousingCost?sessid=" + sessionData().get("sessionId"), "Post", payload)
                .then()
                .statusCode(200)
                .extract()
                .as(DataModel.class);
        System.out.println(model);
        sessionData().put("model", model);
    }

    @Step("Проверка расчета требуемого дохода")
    final void validateIncome(CalculatorRequestModel model) {
        Integer requiredMonthlyIncome = model.get("requiredMonthlyIncome");
        Integer monthlyPayment = model.get("monthlyPayment");
        double comparison = (double) new BigDecimal(monthlyPayment).multiply(new BigDecimal(1000)).divide(new BigDecimal(requiredMonthlyIncome), RoundingMode.HALF_UP)
                .intValue() / 1000;
        Assert.assertEquals(comparison, 0.6, "Требуемый доход расчитан некорректно");
    }

    @Step("Проверка расчета аннуитета")
    final void validateAnnuitet(CalculatorRequestModel model) {
        Integer monthlyPayment = model.get("monthlyPayment");
        Integer creditAmount = model.get("creditAmount");
        Integer period = sessionData().get("creditPeriod");
        Float rate = sessionData().get("interestPercent");

        double monthlyInterest = rate / 100.0 / 12.0;
        double mathPower = Math.pow(1.0 + monthlyInterest, period);

        Integer annuity = (int)(creditAmount * (monthlyInterest * mathPower / (mathPower - 1.0)));

        Assert.assertEquals(annuity, monthlyPayment,"Probably an error in domrf algorith. Their results differ from banki.ru/bancrate.com results");
    }

    @Step("Проверка расчета размера кредита")
    final void validateCreditAmount(@NotNull CalculatorRequestModel model) {
        Assert.assertEquals(model.<Integer>get("creditAmount"), sessionData().<Integer>get("creditAmount"));

    }

    @Step("Проверка расчета переплаты")
    final void validateOverpayment(@NotNull CalculatorRequestModel model) {
        Integer monthlyPayment = model.get("monthlyPayment");
        Integer creditAmount = model.get("creditAmount");
        Integer period = sessionData().get("creditPeriod");
        Integer overpayment = model.get("overpayment");
        Assert.assertEquals((int) overpayment, monthlyPayment * period - creditAmount, "Требуемый доход расчитан некорректно");
    }

    @Step("Проверка ипотечных условий")
    final void validateAnnuitet() {
        DataModel model = sessionData().get("model");
        validateIncome(model.getData());

        validateOverpayment(model.getData());
        validateCreditAmount(model.getData());
        validateAnnuitet(model.getData());
    }


    @SneakyThrows
    @Test
    public void test() {
        initSession();
        prepareData();
        housing();
        calculate();
        validateAnnuitet();
    }
}
