package order;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserResponse;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static user.User.getRandomUser;

public class OrderGetTests {
    private User user;
    private Order order;
    private UserResponse userResponse;
    private OrderResponse orderResponse;
    private String accessToken;

    @Before
    public void setUp() {
        orderResponse = new OrderResponse();
    }

    @Test
    public void getOrdersWithAuthTest() {
        userResponse = new UserResponse();
        user = getRandomUser();
        ValidatableResponse responseUser = userResponse.createUser(user);
        int status = responseUser.extract().statusCode();
        assertEquals(SC_OK, status);
        accessToken = responseUser.extract().path("accessToken");

        ValidatableResponse response = orderResponse.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[4]._id"));
        ingredients.add(response.extract().path("data[6]._id"));
        order = new Order(ingredients);
        ValidatableResponse orderResp = orderResponse.createOrder(order);
        int statusCode = orderResp.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse getResponse = orderResponse.getOrdersByAuthorization(accessToken);
        int getCode = getResponse.extract().statusCode();
        assertEquals(SC_OK, getCode);
        boolean isGetting = getResponse.extract().path("success");
        assertTrue("Orders are not available", isGetting);

        userResponse.deleteUser(accessToken);
    }

    @Test
    public void getOrdersWithoutAuthTest() {
        ValidatableResponse response = orderResponse.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[4]._id"));
        ingredients.add(response.extract().path("data[6]._id"));
        order = new Order(ingredients);
        ValidatableResponse orderResp = orderResponse.createOrder(order);
        int statusCode = orderResp.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse getResponse = orderResponse.getOrdersWithoutAuthorization();
        int getCode = getResponse.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, getCode);
        String message = getResponse.extract().path("message");
        assertEquals("You should be authorised", message);
    }

}
