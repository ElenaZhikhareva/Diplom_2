package user;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserResponse extends Config {
    private final String USER = "/api/auth/user ";


    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        String REGISTER = "/api/auth/register";
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        String LOGIN = "/api/auth/login ";
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
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER)
                .then();
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
    public void deleteUser(String accessToken) {
        given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER)
                .then();
    }
}
