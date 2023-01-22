package order;

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
import static user.User.getRandomUser;


public class OrderCreationTests {
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
    public void createOrderWithoutAuthTest() {
        ValidatableResponse response = orderResponse.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[0]._id"));
        ingredients.add(response.extract().path("data[3]._id"));
        order = new Order(ingredients);
        ValidatableResponse responseOr = orderResponse.createOrderWithoutAuthorization(order);
        int statusCode = responseOr.extract().statusCode();
        assertEquals(SC_OK, statusCode);
        boolean isCreate = response.extract().path("success");
        assertTrue("Order is not created", isCreate);

    }

    @Test
    public void createOrderWithAuthTest() {
        userResponse = new UserResponse();
        user = getRandomUser();
        ValidatableResponse responseUser = userResponse.createUser(user);
        int status = responseUser.extract().statusCode();
        assertEquals(SC_OK, status);
        accessToken = responseUser.extract().path("accessToken");

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
        userResponse.deleteUser(accessToken);
    }

    @Test
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
    public void createOrderWithInvalidIngTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("25896631");
        ingredients.add("1478963");
        order = new Order(ingredients);
        ValidatableResponse response = orderResponse.createOrder(order);
        int statusCode = response.extract().statusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }


    }
