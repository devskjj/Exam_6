package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class MyGenerator {
    private static Random rnd = new Random();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static int generateTime() {
        return 8 + rnd.nextInt(10);
    }

    public static String generateBirthDate() {
        int minAge = 8;
        int maxAge = 90;

        int age = minAge + rnd.nextInt(maxAge - minAge + 1);
        int year = LocalDate.now().getYear() - age;
        int month = 1 + rnd.nextInt(12);
        int day = 1 + rnd.nextInt(31);

        LocalDate birthDate = LocalDate.of(year, month, day);
        return birthDate.format(formatter);
    }

    public static String generateType() {
        return rnd.nextBoolean() ? "Первичный" : "Вторичный";
    }

}
