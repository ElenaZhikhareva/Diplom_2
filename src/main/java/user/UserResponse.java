package user;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserResponse extends Config {
    private final String REGISTER = "/api/auth/register";
    private final String LOGIN = "/api/auth/login ";
    private final String USER = "/api/auth/user ";


    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(LOGIN)
                .then();
    }

    @Step("Изменение данных пользователя с авторизацией")
    public ValidatableResponse updateUserWithAuthorization(User user, String accessToken) {
        return given()
                .spec(getSpec())
                .log().all()
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER)
                .then()
                .log().all();
    }

    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .patch(USER)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER)
                .then();
    }

    public ValidatableResponse userGetInfo(User user) {
        return given().log().all()
                .spec(getSpec())
                .header("Authorization", user.getAccessToken())
                .when()
                .get(USER)
                .then().log().all();
    }
}
