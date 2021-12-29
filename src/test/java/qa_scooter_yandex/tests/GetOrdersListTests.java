package qa_scooter_yandex.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import qa_scooter_yandex.model.Order;
import qa_scooter_yandex.rests.OrdersAPI;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GetOrdersListTests {

    private final OrdersAPI order = new OrdersAPI();

    @Before
    public void setup() {
        Order orders = new Order(new String[]{"BLACK"}, 4);
        String orderTrackNumber =
                order.createNewOrder(orders).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
    }

    @Test
    @DisplayName("Orders list by courier not empty")
    public void checkNonEmptyListIsReturned() {
        ValidatableResponse response = order.getOrdersList();
        List<Object> orderList = response.extract().jsonPath().getList("orders");

        response.assertThat().statusCode(200);
        assertThat(orderList.isEmpty(), is(false));
    }
}