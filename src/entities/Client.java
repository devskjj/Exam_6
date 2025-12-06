package entities;

import java.time.LocalDateTime;

public class Client {

    private String firstName;
    private String lastName;
    private String surName;
    private String birthDate;
    private String clientType;
    private String symptoms;
    private int time;

    public Client(String firstName, String lastName, String surName, String birthDate, String clientType, String symptoms, int time) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.surName = surName;
        this.birthDate = birthDate;
        this.clientType = clientType;
        this.symptoms = symptoms;
        this.time = time;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSurName() {
        return surName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getClientType() {
        return clientType;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public int getTime() {
        return time;
    }
}
