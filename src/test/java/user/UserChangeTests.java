package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import static user.User.getRandomUser;

public class UserChangeTests {
    User user;
    UserResponse userResponse;
    private String accessToken;

    @Before
    public void setup() {
        userResponse = new UserResponse();
        user = getRandomUser();
    }

    @Test
    @DisplayName("Проверить что авторизованный пользователь может изменить любое поле данных")
    public void changeDataUserWithAutorization() {
        ValidatableResponse responseCreate = userResponse.createUser(user);
        UserCredentials credentials = new UserCredentials(user.getEmail(), user.getPassword());
        ValidatableResponse responseLogin = userResponse.loginUser(credentials);
        accessToken = responseLogin.extract().path("accessToken");
        String userUpdateName = user.getName() + "new";
        user.setName(userUpdateName);
        user.setAccessToken(responseCreate.extract().path("accessToken"));
        ValidatableResponse responseUpdate = userResponse.updateUserWithAuthorization(user, accessToken);
        int statusCode = responseUpdate.extract().statusCode();
        Assert.assertEquals("User not updated", SC_OK, statusCode);
        String actualName = responseUpdate.extract().path("user.name");
        Assert.assertEquals("Name failed", userUpdateName, actualName);

    }

    @Test
    @DisplayName("Изменение данных пользователя: без авторизации")
    public void updateUserDataWithoutAuth() {
        String userUpdateName = user.getName() + "new2";
        user.setName(userUpdateName);
        ValidatableResponse responseUpdate = userResponse.updateUserWithoutAuthorization(user);
        int statusCode = responseUpdate.extract().statusCode();
        Assert.assertEquals("You should be authorised", SC_UNAUTHORIZED, statusCode);
        String actualName = responseUpdate.extract().path("user.name");
        Assert.assertNotEquals("E-mail в ответе сервера не совпадает актуальным", userUpdateName, actualName);
    }

    @After
    public void delete() {
        if (accessToken != null) {
            userResponse.deleteUser(accessToken);
        }
    }
}
