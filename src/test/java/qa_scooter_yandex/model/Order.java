package qa_scooter_yandex.model;


import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Order {

    private final String firstName;
    private final String lastName;
    private final String address;
    private final int metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final String[] color;

    public Order(String[] color, int metroStationId) {
        Faker faker = new Faker(new Locale("ru"));
        this.color = color;
        this.metroStation = metroStationId;
        this.firstName = faker.name().firstName();
        this.lastName = faker.name().lastName();
        this.address = faker.address().fullAddress();
        this.phone = faker.phoneNumber().phoneNumber();
        this.deliveryDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.comment = RandomStringUtils.randomAlphabetic(20);
        this.rentTime = (int) (Math.random() * 6) + 1;

    }

}
