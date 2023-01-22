package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertTrue;

public class UserCreationTests {

    User user;
    UserResponse userResponse;
    private String accessToken;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        userResponse = new UserResponse();
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUserTest() {
        user = User.getRandomUser();
        ValidatableResponse responseCreate = userResponse.createUser(user);
        int statusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("User not updated", SC_OK, statusCode);
        boolean isCreated = responseCreate.extract().path("success");
        assertTrue("User is not created", isCreated);

        accessToken = responseCreate.extract().path("accessToken");
        userResponse.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже есть в системе")
    public void userCreateAlreadyExistsTest() {
        user = User.getRandomUser();
        ValidatableResponse responseCreate = userResponse.createUser(user);
        int statusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("User not updated", SC_OK, statusCode);
        boolean isCreated = responseCreate.extract().path("success");
        assertTrue("User is not created", isCreated);

        ValidatableResponse responseCreate1 = userResponse.createUser(user);
        int statusCodeForbidden = responseCreate1.extract().statusCode();
        Assert.assertEquals("User not updated", SC_FORBIDDEN, statusCodeForbidden);

        accessToken = responseCreate.extract().path("accessToken");
        userResponse.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Проверка невозможности создания пользователя, если не заполнено поле логин")
    public void userCreateWithoutLoginTest() {
        user = User.getWithoutLogin();
        ValidatableResponse responseCreate = userResponse.createUser(user);
        int statusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("User not updated", SC_FORBIDDEN, statusCode);
    }

    @Test
    @DisplayName("Проверка невозможности создания пользователя, если не заполнено поле пароль")
    public void userCreateWithoutPasswordTest() {
        user = User.getWithoutPassword();
        ValidatableResponse responseCreate = userResponse.createUser(user);
        int statusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("User not updated", SC_FORBIDDEN, statusCode);
    }
}
