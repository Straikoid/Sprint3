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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Orders management")
@Story("Accept order")
public class AcceptOrderTests {

    private final CourierAccountAPI courierAccountAPI = new CourierAccountAPI();
    private final OrdersAPI ordersAPI = new OrdersAPI();
    private String courierId;
    private CourierAccount courierAccount;
    private Order order;
    private qa_scooter_yandex.model.CourierCredentials CourierCredentials;


    @Before
    public void setup() {
        courierAccount = CourierAccount.getRandom();
        CourierCredentials = new CourierCredentials(courierAccount.getLogin(), courierAccount.getPassword());
        courierId=null;
        order = new Order(new String[]{"BLACK"}, 4);
    }


    @Test
    @DisplayName("Accept new order with correct order ID for courier with correct ID")
    public void acceptNewCorrectOrderSuccess() {

        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        String orderTrackNumber = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
        String orderId = ordersAPI.getOrderByTrackNumber(orderTrackNumber).assertThat().statusCode(SC_OK).extract().path("order.id").toString();
        courierId = courierAccountAPI.loginCourierAccount(CourierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId(orderId, courierId);
        response.assertThat().statusCode(SC_OK).and().body("ok", equalTo(true));


    }

    @Test
    @DisplayName("Accept new order with empty order ID for courier with correct ID")
    public void acceptOrderWithoutOrderNumberBadRequest() {

        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        courierId = courierAccountAPI.loginCourierAccount(CourierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId("", courierId);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для поиска"));


    }

    @Test
    @DisplayName("Accept new order with correct order ID without courier")
    public void acceptNewCorrectOrderWithoutCourierIdConflict() {

        String orderTrackNumber = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
        String orderId = ordersAPI.getOrderByTrackNumber(orderTrackNumber).assertThat().statusCode(SC_OK).extract().path("order.id").toString();
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId(orderId, "");
        response.assertThat().assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для поиска"));

    }

    @Test
    @DisplayName("Accept new order with wrong order ID for courier with correct ID")
    public void acceptOrderWithWrongOrderNumberNotFound() {

        String orderId = String.valueOf(1000000 + (int) (Math.random() * 2000000));
        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        courierId = courierAccountAPI.loginCourierAccount(CourierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId(orderId, courierId);
        response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo("Заказа с таким id не существует"));

    }

    @Test
    @DisplayName("Accept new order with correct order ID for courier with wrong ID")
    public void acceptNewCorrectOrderWithWrongCourierIdNotFound() {

        String orderTrackNumber = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
        String orderId = ordersAPI.getOrderByTrackNumber(orderTrackNumber).assertThat().statusCode(SC_OK).extract().path("order.id").toString();
        String courierId = String.valueOf(1000000 + (int) (Math.random() * 2000000));
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId(orderId, courierId);
        response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo("Курьера с таким id не существует"));

    }


    @Test
    @DisplayName("Accept already accepted order")
    public void acceptAlreadyAcceptedOrderConflict() {

        courierAccountAPI.registerNewCourierAccount(courierAccount).assertThat().statusCode(SC_CREATED);
        String orderTrackNumber = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track").toString();
        String orderId = ordersAPI.getOrderByTrackNumber(orderTrackNumber).assertThat().statusCode(SC_OK).extract().path("order.id").toString();
        courierId = courierAccountAPI.loginCourierAccount(CourierCredentials).assertThat().statusCode(SC_OK).extract().path("id").toString();
        ValidatableResponse response = ordersAPI.acceptOrderByOrderId(orderId, courierId);
        response.assertThat().statusCode(SC_OK).and().body("ok", equalTo(true));
        response = ordersAPI.acceptOrderByOrderId(orderId, courierId);
        response.assertThat().assertThat().statusCode(SC_CONFLICT).and().body("message", equalTo("Этот заказ уже в работе"));


    }

    @After
    public void tearDown() {
        if(courierId!=null)
        courierAccountAPI.deleteCourierAccount(courierId).assertThat().statusCode(SC_OK);

    }
}
