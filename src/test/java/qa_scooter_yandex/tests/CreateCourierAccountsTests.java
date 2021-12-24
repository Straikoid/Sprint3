package qa_scooter_yandex.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import qa_scooter_yandex.model.CourierAccount;

import qa_scooter_yandex.model.CourierAccountAPI;
import qa_scooter_yandex.model.CourierCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Courier accounts management")
@Story("Create new account")
public class CreateCourierAccountsTests {

    private final CourierAccountAPI courierAccountAPI = new CourierAccountAPI();
    private int courierId;
    private CourierAccount courierAccount;
    private CourierCredentials credentials;


    @Before
    public void setup() {
        courierAccount = CourierAccount.getRandom();
        credentials = new CourierCredentials(courierAccount.getLogin(), courierAccount.getPassword());
        courierId=-1;
    }

    @Test
    @DisplayName("Register account with unique  username and with password")
    public void registerNewCourierAccountWithUniqueUsernameSuccess() {


        ValidatableResponse response = courierAccountAPI.registerNewCourierAccount(courierAccount);
        response.assertThat().statusCode(SC_CREATED).and().body("ok", equalTo(true));
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");

    }

    @Test
    @DisplayName("Register account with unique username and without password")
    public void registerNewCourierAccountWithEmptyPasswordBadRequest() {

        courierAccount.setPassword(null);
        ValidatableResponse response = courierAccountAPI.registerNewCourierAccount(courierAccount);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Register account with empty username and with password")
    public void registerNewCourierAccountWithEmptyUsernameBadRequest() {
        courierAccount.setLogin(null);
        ValidatableResponse response = courierAccountAPI.registerNewCourierAccount(courierAccount);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }


    @Test
    @DisplayName("Register account with empty FirstName and with unique username/password")
    public void registerNewCourierAccountWithEmptyFirstNameSuccess() {

        courierAccount.setFirstName(null);
        ValidatableResponse response = courierAccountAPI.registerNewCourierAccount(courierAccount);
        response.assertThat().statusCode(SC_CREATED).and().body("ok", equalTo(true));
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(200).extract().path("id");

    }

    @Test
    @DisplayName("Register account with duplicated username and with password")
    public void registerNewCourierAccountWithDuplicatedUsernameConflict() {

        courierAccountAPI.registerNewCourierAccount(courierAccount);
        CourierAccount secondAccount = CourierAccount.getRandom();
        secondAccount.setLogin(courierAccount.getLogin());
        ValidatableResponse secondAccountResponse = courierAccountAPI.registerNewCourierAccount(secondAccount);
        secondAccountResponse.assertThat().statusCode(SC_CONFLICT).and().body("message", equalTo("Этот логин уже используется"));
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");


    }

    @After
    public void tearDown() {
        if(courierId!=-1) {
            courierAccountAPI.deleteCourierAccount(String.valueOf(courierId)).assertThat().statusCode(SC_OK);
        }

    }

}

