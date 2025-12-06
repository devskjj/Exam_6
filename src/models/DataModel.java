package models;

import entities.Client;
import utility.Generator;
import utility.MyGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DataModel {

    private transient Client client;
    private List<Client> clients = new ArrayList<>();

    public void generateClients() {
        if (clients.isEmpty()) {
            for (int i = 0; i <= 5; i++) {
                clients.add(new Client(Generator.makeName(),
                        Generator.makeName(),
                        Generator.makeName(),
                        MyGenerator.generateBirthDate(),
                        MyGenerator.generateType(),
                        Generator.makeName(),
                        MyGenerator.generateTime()));
            }
            clients.sort(Comparator.comparing(Client::getTime));
        }
    }

    public Client removeRandomClient() {
        Random rnd = new Random();

        if (clients.isEmpty()) return null;
        int index = rnd.nextInt(clients.size());
        return clients.remove(index);
    }

    public void addRandomClient() {
        int min = 8;
        int max = 17;
        int maxNumber = max - min + 1;

        if (clients.size() >= maxNumber) {
            return;
        }

        clients.add(new Client(
                Generator.makeName(),
                Generator.makeName(),
                Generator.makeName(),
                MyGenerator.generateBirthDate(),
                MyGenerator.generateType(),
                Generator.makeName(),
                generateUniqueTime()
        ));
        clients.sort(Comparator.comparing(Client::getTime));
    }

    private int generateUniqueTime() {
        while (true) {
            int newTime = MyGenerator.generateTime();
            boolean exists = clients.stream()
                    .anyMatch(c -> c.getTime() == newTime);
            if (!exists) return newTime;
        }
    }



    public List<Client> getClients() {
        return clients;
    }
}
