package user;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserResponse extends Config {
    private final String REGISTER = "/auth/register";
    private final String LOGIN = "/auth/login ";
    private final String USER = "/auth/user ";
    private final String ORDERS = "/orders ";
    private final String INGREDIENTS = "/ingredients ";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(REGISTER)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .header("Content-type", "application/json")
                .and()
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
                .log().all()
                .body(user)
                .patch(USER)
                .then()
                .log().all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(User user) {
        UserCredentials creds = UserCredentials.from(user);
        String token = loginUser(creds).extract().path("accessToken");
        return given()
                .header("Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(user)
                .auth().oauth2(token.substring(7))
                .when()
                .delete(USER)
                .then();
    }
}
