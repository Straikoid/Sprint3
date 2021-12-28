package qa_scooter_yandex.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import qa_scooter_yandex.model.*;
import qa_scooter_yandex.rests.CourierAccountAPI;
import qa_scooter_yandex.rests.OrdersAPI;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@Feature("Orders management")
@Story("Get Orders List")

public class GetOrdersListTests {
    private final CourierAccountAPI courierAccountAPI = new CourierAccountAPI();
    private final OrdersAPI ordersAPI = new OrdersAPI();
    private ArrayList<Order> ordersList;
    ArrayList<String> ordersTrackNumbers;
    CourierAccount courierAccount;
    CourierCredentials courierCredentials;
    String courierId;

    @Before
    public void setup() {
        ordersList = new ArrayList<>(10);
        ordersTrackNumbers = new ArrayList<>();
        courierAccount = CourierAccount.getRandom();
        courierCredentials = new CourierCredentials(courierAccount.getLogin(), courierAccount.getPassword());
        courierId = null;
    }

    @Test
    @DisplayName("Get orders list for courier with correct ID and check result")
    public void getOrdersListForCertainCourierByCourierIdAndCheckReceivedListSuccess() {

        createOrdersList();
        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        courierId = courierAccountAPI.loginCourierAccount(courierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        createAndAcceptCompleteOrdersForCourier(courierId);
        ValidatableResponse response = ordersAPI.getOrdersListForCourier(courierId, null, null, null);
        response.assertThat().statusCode(SC_OK).and().body("orders.size()", is(10));

    }

    @Test
    @DisplayName("Get available orders list for any courier and check result")
    public void getOrdersListForAnyCourierAndCheckReceivedListSuccess() {

        createOrdersList();
        fillOrdersTrackNumbersList();
        ValidatableResponse response = ordersAPI.getOrdersListForCourier("", null, null, null);
        response.assertThat().statusCode(SC_OK).and().body("orders.size()", is(greaterThanOrEqualTo(10)));

    }

    @Test
    @DisplayName("Get orders list for courier with correct ID near metrostations and check result")
    public void getOrdersListForCertainCourierNearStationsAndCheckReceivedListSuccess() {


        for (int i = 0; i < 10; i++) {
            if (i < 3) {
                ordersList.add(new Order(new String[]{"BLACK"}, 5));
            } else if (i < 8) {
                ordersList.add(new Order(new String[]{"BLACK"}, 6));
            } else {
                ordersList.add(new Order(new String[]{"BLACK"}, 7));
            }
        }

        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        courierId = courierAccountAPI.loginCourierAccount(courierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        createAndAcceptCompleteOrdersForCourier(courierId);
        ValidatableResponse response = ordersAPI.getOrdersListForCourier(courierId, "[\"5\", \"6\"]", null, null);
        response.assertThat().statusCode(SC_OK).and().body("orders.size()", is(8));


    }

    @Test
    @DisplayName("Get orders list available for any courier and check result")
    public void getOrdersListAvailableForAnyCourierAndCheckReceivedListSuccess() {

        createOrdersList();
        fillOrdersTrackNumbersList();
        ValidatableResponse response = ordersAPI.getOrdersListForCourier("", null, "10", "0");
        response.assertThat().statusCode(SC_OK).and().body("orders.size()", is(10));

    }

    @Test
    @DisplayName("Get orders list available for any courier near metrostations and check result")
    public void getTenOrdersAvailableForAnyCourierNearStationAndCheckReceivedListSuccess() {

        createOrdersList();
        fillOrdersTrackNumbersList();
        ValidatableResponse response = ordersAPI.getOrdersListForCourier("", "[\"15\"]", "10", "0");
        response.assertThat().statusCode(SC_OK).and().body("orders.size()", is((10)));

    }

    @After
    public void tearDown() {
        for (String trackNumber : ordersTrackNumbers) {
            ordersAPI.cancelOrderByTrackNumber(trackNumber).assertThat().statusCode(SC_OK);
        }
        if (courierId!=null) {
            courierAccountAPI.deleteCourierAccount(courierId).assertThat().statusCode(SC_OK);
        }
    }

    private void createOrdersList() {
        for (int i = 0; i < 10; i++) {
            ordersList.add(new Order(new String[]{"BLACK"}, 1));
        }
    }

    private void fillOrdersTrackNumbersList() {
        for (Order order : ordersList) {
            String orderTrackNumber = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
            ordersTrackNumbers.add(orderTrackNumber);
        }
    }

    private void createAndAcceptCompleteOrdersForCourier(String courierId) {
        for (int i = 0; i < ordersList.size(); i++) {
            String orderTrackNumber = ordersAPI.createNewOrder(ordersList.get(i)).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
            String orderId = ordersAPI.getOrderByTrackNumber(String.valueOf(orderTrackNumber)).assertThat().statusCode(SC_OK).extract().path("order.id").toString();
            ordersAPI.acceptOrderByOrderId(orderId, courierId).assertThat().statusCode(SC_OK);
            if (i < (ordersList.size() / 2)) {
                ordersAPI.completeOrderByOrderId(orderId).assertThat().statusCode(SC_OK);
            }
        }

    }
}
