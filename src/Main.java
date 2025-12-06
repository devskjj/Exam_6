import models.DataModel;
import server.ServerLogic;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            DataModel dataModel = new DataModel();
            new ServerLogic("localhost", 9889, dataModel).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
