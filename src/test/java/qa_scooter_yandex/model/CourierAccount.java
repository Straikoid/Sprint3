package qa_scooter_yandex.model;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierAccount  {

    private String login;
    private String password;
    private String firstName;

    public CourierAccount(String login, String password, String firstName)
    {
        this.login = login;
        this.password=password;
        this.firstName=firstName;


    }
    public static CourierAccount getRandom()
    {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password= RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        return new CourierAccount(login,password,firstName);

    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }



    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
