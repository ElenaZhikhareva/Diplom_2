package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserResponse;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderCreationTests {
    private Order order;
    private OrderResponse orderResponse;
    private String accessToken;
    User user;
    UserResponse userResponse;


    @Before
    public void setUp() {
        orderResponse = new OrderResponse();
        user = User.getRandomUser();
        userResponse = new UserResponse();
        ValidatableResponse responseUser = userResponse.createUser(user);
        accessToken = responseUser.extract().path("accessToken");
    }

    @DisplayName("Создание заказа без авторизации")
    @Test
    public void createOrderWithoutAuthTest() {
        ValidatableResponse response = orderResponse.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[0]._id"));
        ingredients.add(response.extract().path("data[3]._id"));
        Order order = new Order(ingredients);
        ValidatableResponse responseOr = orderResponse.createOrderWithoutAuthorization(order);
        int statusCode = responseOr.extract().statusCode();
        assertEquals(SC_OK, statusCode);
        boolean isCreate = response.extract().path("success");
        assertTrue("Order is not created", isCreate);
    }

    @DisplayName("Создание заказа с авторизацией, с ингридиентами")
    @Test
    public void createOrderWithAuthTest() {
        ValidatableResponse response = orderResponse.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[2]._id"));
        ingredients.add(response.extract().path("data[4]._id"));
        order = new Order(ingredients);

        ValidatableResponse responseOrder = orderResponse.createOrderByAuthorization(accessToken, order);
        int statusCode = responseOrder.extract().statusCode();
        assertEquals(SC_OK, statusCode);
        String order = responseOrder.extract().body().asString();
        assertThat(order, containsString("status"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без ингридиентами")
    public void createOrderWithoutIngredientsTest() {
        List<String> ingredients = new ArrayList<>();
        order = new Order(ingredients);
        ValidatableResponse response = orderResponse.createOrder(order);
        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);
        String message = response.extract().path("message");
        assertEquals("Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов.")
    public void createOrderWithInvalidIngTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("1111111");
        ingredients.add("1234567");
        order = new Order(ingredients);
        ValidatableResponse response = orderResponse.createOrder(order);
        int statusCode = response.extract().statusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @After
    public void delete() {
        if (accessToken != null) {
            userResponse.deleteUser(accessToken);
        }
    }
}
