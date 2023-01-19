package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserLoginTests {
    User user;
    UserResponse userResponse;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        user = User.getRandomUser();
        userResponse = new UserResponse();
        userResponse.createUser(user);
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
        UserCredentials creds = UserCredentials.getWrongLoginPassword(user);
        String massage = userResponse.loginUser(creds).extract().path("message");
        assertEquals("email or password are incorrect", massage);
    }

    @After
    public void delete() {
        userResponse.deleteUser(user);
    }
}
