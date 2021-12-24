package qa_scooter_yandex.tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import qa_scooter_yandex.model.CourierAccount;
import qa_scooter_yandex.model.CourierAccountAPI;
import qa_scooter_yandex.model.CourierCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Courier accounts management")
@Story("Login under Courier account")
public class LoginCourierAccountTests {

    private final CourierAccountAPI courierAccountAPI = new CourierAccountAPI();
    private int courierId;
    private CourierAccount courierAccount;

    @Before
    public void setup() {
        courierAccount = CourierAccount.getRandom();
        courierAccountAPI.registerNewCourierAccount(courierAccount);
    }

    @Test
    @DisplayName("Login with correct username/password")
    public void loginCourierAccountCorrectCreditsSuccess() {

        CourierCredentials credentials = new CourierCredentials(courierAccount.getLogin(), courierAccount.getPassword());
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_OK).and().body("id", notNullValue());
        courierId = response.assertThat().extract().path("id");
    }
    @Test
    @DisplayName("Login without password")
    public void loginCourierAccountWithoutPasswordBadRequest()  {

        CourierCredentials credentials = new CourierCredentials(courierAccount.getLogin(), null);
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для входа"));
        credentials.setPassword(courierAccount.getPassword());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");
    }

    @Test
    @DisplayName("Login with Empty password")
    public void loginCourierAccountWithEmptyPasswordBadRequest()  {

        CourierCredentials credentials = new CourierCredentials(courierAccount.getLogin(), "");
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для входа"));
        credentials.setPassword(courierAccount.getPassword());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");
    }

    @Test
    @DisplayName("Login without username")
    public void loginCourierAccountWithoutUserNameBadRequest()  {

        CourierCredentials credentials = new CourierCredentials(null, courierAccount.getPassword());
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для входа"));
        credentials.setLogin(courierAccount.getLogin());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");
    }

    @Test
    @DisplayName("Login with Empty username")
    public void loginCourierAccountWithEmptyUserNameBadRequest()  {

        CourierCredentials credentials = new CourierCredentials("", courierAccount.getPassword());
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_BAD_REQUEST).and().body("message", equalTo("Недостаточно данных для входа"));
        credentials.setLogin(courierAccount.getLogin());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");
    }

    @Test
    @DisplayName("Login with wrong password")
    public void loginCourierAccountWithWrongPasswordNotFound()  {

        CourierCredentials credentials = new CourierCredentials(courierAccount.getLogin(), RandomStringUtils.randomAlphabetic(10));
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo("Учетная запись не найдена"));
        credentials.setPassword(courierAccount.getPassword());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");


    }

    @Test
    @DisplayName("Login with not existed username")
    public void loginCourierAccountWithWrongUserNameNotFound()  {

        CourierCredentials credentials = new CourierCredentials(RandomStringUtils.randomAlphabetic(10), courierAccount.getPassword());
        ValidatableResponse response = courierAccountAPI.loginCourierAccount(credentials);
        response.assertThat().statusCode(SC_NOT_FOUND).and().body("message", equalTo("Учетная запись не найдена"));
        credentials.setLogin(courierAccount.getLogin());
        courierId = courierAccountAPI.loginCourierAccount(credentials).assertThat().statusCode(SC_OK).extract().path("id");
    }

    @After
    public void tearDown() {
        courierAccountAPI.deleteCourierAccount(String.valueOf(courierId)).assertThat().statusCode(SC_OK);
    }

}
