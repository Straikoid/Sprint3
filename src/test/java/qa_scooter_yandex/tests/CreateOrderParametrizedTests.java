package qa_scooter_yandex.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import qa_scooter_yandex.model.Order;
import qa_scooter_yandex.rests.OrdersAPI;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;


@Feature("Orders management")
@Story("Create new order")
@RunWith(Parameterized.class)
public class CreateOrderParametrizedTests {

    private final String[] color;
    private final int expectedStatus;
    private final String expectedBody;
    private final OrdersAPI ordersAPI = new OrdersAPI();


    public CreateOrderParametrizedTests(String[] color, int expectedStatus, String expectedBody) {
        this.color = color;
        this.expectedStatus = expectedStatus;
        this.expectedBody = expectedBody;

    }

    @Parameterized.Parameters
    public static Object[][] getDataForTests() {
        return new Object[][]{
                {new String[]{"BLACK"}, 201, "track"},
                {new String[]{"GREY"}, 201, "track"},
                {new String[]{"BLACK, GREY"}, 201, "track"},
                {new String[]{"BLACK, GREY"}, 201, "track"},
                {null, 201, "track"}
        };
    }

    @Test
    @DisplayName("Create orders with different Colors value")
    public void createNewOrderSuccess() {
        Order order = new Order(color, 4);
        ValidatableResponse response = ordersAPI.createNewOrder(order);
        String orderTrackNumber = response.assertThat().statusCode(expectedStatus).and().body(expectedBody, notNullValue()).extract().path("track").toString();
        ordersAPI.cancelOrderByTrackNumber(orderTrackNumber).assertThat().statusCode(SC_OK);

    }
}





