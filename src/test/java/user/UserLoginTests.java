package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserLoginTests {
    User user;
    UserResponse userResponse;
    private String accessToken;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        user = User.getRandomUser();
        userResponse = new UserResponse();
        ValidatableResponse responseUser = userResponse.createUser(user);
        accessToken = responseUser.extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка того, что можно залогиниться под существующим пользователем")
    public void userloginTest() {
        UserCredentials creds = UserCredentials.from(user);
        Boolean isOk = userResponse.loginUser(creds).extract().path("success");
        assertTrue(isOk);
    }

    @Test
    @DisplayName("Проверка того, что нельзя залогиниться с неверным логином и паролем")
    public void userloginTestWrongLoginPassword() {
        userResponse.createUser(user);
        UserCredentials creds = UserCredentials.getWrongLoginPassword();
        String massage = userResponse.loginUser(creds).extract().path("message");
        assertEquals("email or password are incorrect", massage);
    }

    @After
    public void delete() {
        if (accessToken != null) {
            userResponse.deleteUser(accessToken);
        }
    }
}
