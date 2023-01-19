package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserCreationTests {

    User user;
    UserResponse userResponse;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        user = User.getRandomUser();
        userResponse = new UserResponse();
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUserTest() {
        userResponse.createUser(user)
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже есть в системе")
    public void userCreateAlreadyExistsTest() {
        userResponse.createUser(user);
        String userAlreadyExists = userResponse.createUser(user)
                .assertThat()
                .statusCode(403)
                .extract().path("message");
        assertEquals("User already exists", userAlreadyExists);
    }

    @Test
    @DisplayName("Проверка невозможности создания пользователя, если не заполнено поле логин")
    public void userCreateWithoutLoginTest() {
        userResponse.createUser(user);
        User getWithoutLogin = User.getWithoutLogin();
        String expected = userResponse.createUser(getWithoutLogin)
                .assertThat()
                .statusCode(403)
                .extract().path("message");
        assertEquals("Email, password and name are required fields", expected);
    }

    @Test
    @DisplayName("Проверка невозможности создания пользователя, если не заполнено поле пароль")
    public void userCreateWithoutPasswordTest() {
        userResponse.createUser(user);
        User userWithoutPassword = User.getWithoutPassword();
        String expected = userResponse.createUser(userWithoutPassword)
                .assertThat()
                .statusCode(403)
                .extract().path("message");
        assertEquals("Email, password and name are required fields", expected);
    }

    @After
    public void delete() {
        userResponse.deleteUser(user);
    }
}
