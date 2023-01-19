package user;

import com.google.gson.JsonObject;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;

public class UserChangeTests {
    User user;
    UserResponse userResponse;
    private static String accessToken;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        user = User.getRandomUser();
        userResponse = new UserResponse();
    }


    @Test
    @DisplayName("Проверить что авторизованный пользователь может изменить любое поле данных")
    public void changeDataUserWithAutorization() {
        ValidatableResponse responseCreate = userResponse.createUser(user);
        UserCredentials credentials = UserCredentials.from(user);
        ValidatableResponse responseLogin = userResponse.loginUser(credentials);
        String accessToken = responseCreate.extract().path("accessToken");

        String userUpdateEmail = "new" + user.getEmail();
        String userUpdateName = user.getName() + "new";
        user.setEmail(userUpdateEmail);
        user.setName(userUpdateName);
        ValidatableResponse responseUpdate = userResponse.updateUserWithAuthorization(user, accessToken);

        int statusCode = responseUpdate.extract().statusCode();
        Assert.assertEquals("User not updated", 200, statusCode);
        String actualEmail = responseUpdate.extract().path("user.email");
        Assert.assertEquals("E-mail failed", userUpdateEmail, actualEmail);
        String actualName = responseUpdate.extract().path("user.name");
        Assert.assertEquals("Name failed", userUpdateName, actualName);
    }

    @After
    public void delete() {
        userResponse.deleteUser(user);
    }
}
