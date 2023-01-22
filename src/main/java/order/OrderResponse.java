package order;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderResponse extends Config {
    private static final String GET_INGREDIENTS = "/api/ingredients";
    private static final String ORDER_CREATE = "/api/orders";

    @Step("Получение списка ингридиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post("/api/orders/")
                .then();
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrderByAuthorization(String accessToken, Order order) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .post(ORDER_CREATE)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .post(ORDER_CREATE)
                .then();
    }

    @Step("Получение заказа с авторизацией")
    public ValidatableResponse getOrdersByAuthorization(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .get(ORDER_CREATE)
                .then();
    }

    @Step("Получение заказа без авторизации")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getSpec())
                .get(ORDER_CREATE)
                .then();
    }
}
