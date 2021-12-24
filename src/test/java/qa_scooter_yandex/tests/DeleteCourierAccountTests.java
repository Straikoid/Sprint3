package qa_scooter_yandex.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import qa_scooter_yandex.model.CourierAccount;
import qa_scooter_yandex.model.CourierAccountAPI;
import qa_scooter_yandex.model.CourierCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;



@Feature("Courier accounts management")
@Story("Delete account")

public class DeleteCourierAccountTests {

    private final CourierAccountAPI courierAccountAPI = new CourierAccountAPI();



    @Test
    @DisplayName("Delete courier account by correct ID")
    public void deleteCourierAccountByCorrectIdSuccess()  {

        CourierAccount testAccount = CourierAccount.getRandom();
        CourierCredentials credentials = new CourierCredentials(testAccount.getLogin(),testAccount.getPassword());
        courierAccountAPI.registerNewCourierAccount(testAccount);
        courierAccountAPI.loginCourierAccount(credentials);
        String courierId = courierAccountAPI.loginCourierAccount(credentials).extract().path("id").toString();
        ValidatableResponse response = courierAccountAPI.deleteCourierAccount(courierId);
        response.assertThat().statusCode(SC_OK).and().body("ok", equalTo(true));

    }
    @Test
    @DisplayName("Delete courier account without ID")
    public void deleteCourierAccountWithEmptyIdBadRequest() {

        ValidatableResponse response = courierAccountAPI.deleteCourierAccount("");
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo( "Недостаточно данных для удаления курьера"));

    }
    @Test
    @DisplayName("Delete courier account by wrong ID")
    public void deleteCourierAccountWithWrongIdBadRequest() {

        int courierId  = (1000000 + (int) (Math.random() * 2000000));
        ValidatableResponse response = courierAccountAPI.deleteCourierAccount(String.valueOf(courierId));
        response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo( "Курьера с таким id нет."));

    }


}
