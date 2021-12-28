package qa_scooter_yandex.rests;

import io.qameta.allure.Step;

import io.restassured.response.ValidatableResponse;
import qa_scooter_yandex.model.Order;

import static io.restassured.RestAssured.given;

public class OrdersAPI extends RestAssuredClient {

    private static final String ORDER_PATH = "/orders";

    @Step("Create new order")
    public ValidatableResponse createNewOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(order)
                .when()
                .post(ORDER_PATH).then();
    }

    @Step("Get order by TrackNumber")
    public ValidatableResponse getOrderByTrackNumber(String orderTrackNumber) {

        return given()
                .spec(getBaseSpec())
                .and()
                .when().queryParam("t", orderTrackNumber)
                .get(ORDER_PATH + "/track").then();
    }

    @Step("Get orders list")
    public ValidatableResponse getOrdersListForCourier(String courierId, String stationsId, String limit, String page) {

        return given()
                .spec(getBaseSpec())
                .and()
                .when().queryParam("courierId", courierId)
                .queryParam("nearestStation", stationsId)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .get(ORDER_PATH).then();

    }

    @Step("Accept order by Id")
    public ValidatableResponse acceptOrderByOrderId(String orderId, String courierId) {

        return given()
                .spec(getBaseSpec())
                .and()
                .when().queryParam("courierId", courierId)
                .put(ORDER_PATH + "/accept/{orderId}", orderId).then();
    }

    @Step("Complete order by Id")
    public ValidatableResponse completeOrderByOrderId(String orderId) {
        return given()
                .spec(getBaseSpec())
                .and()
                .put(ORDER_PATH + "/finish/{orderId}", orderId).then();

    }

    @Step("Delete order that was created for testing purpose")
    public ValidatableResponse cancelOrderByTrackNumber(String orderTrackNumber) {
        return given().
                spec(getBaseSpec())
                .and()
                .when()
                .queryParam("track", orderTrackNumber)
                .put(ORDER_PATH + "/cancel").then();
    }
}
