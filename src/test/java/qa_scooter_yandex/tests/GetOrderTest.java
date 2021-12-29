package qa_scooter_yandex.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import qa_scooter_yandex.model.Order;
import qa_scooter_yandex.rests.OrdersAPI;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;


@Feature("Orders management")
@Story("Gets single order")
public class GetOrderTest {

    OrdersAPI ordersAPI = new OrdersAPI();



    @Test
    @DisplayName("Get orders by correct TrackNumber")
    public void getOrderByCorrectTrackNumberSuccess()
    {
        Order order = new Order(new String[]{"BLACK"},4);
        int orderTrack = ordersAPI.createNewOrder(order).assertThat().statusCode(SC_CREATED).extract().path("track");
        ValidatableResponse response = ordersAPI.getOrderByTrackNumber(String.valueOf(orderTrack));
        response.assertThat().statusCode(SC_OK).and().body("order.track", equalTo(orderTrack));
        ordersAPI.cancelOrderByTrackNumber(String.valueOf(orderTrack)).assertThat().statusCode(SC_OK);

    }

    @Test
    @DisplayName("Get orders by empty TrackNumber")
    public void getOrderByEmptyTrackNumberBadRequest()
    {
        ValidatableResponse response = ordersAPI.getOrderByTrackNumber("");
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Get orders by wrong TrackNumber")
    public void getOrderByWrongTrackNumberNotFound()
    {
        int id  = (1000000 + (int) (Math.random() * 2000000));
        ValidatableResponse response = ordersAPI.getOrderByTrackNumber(String.valueOf(id));
       response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo("Заказ не найден"));
    }

}
