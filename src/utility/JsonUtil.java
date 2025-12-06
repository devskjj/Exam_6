package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.DataModel;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonUtil {

    private static Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    public static DataModel load(String file) throws IOException {
        Path path = Path.of("src/json", file);

        try (FileReader reader = new FileReader(path.toFile())) {
            return getGson().fromJson(reader, DataModel.class);
        } catch (IOException e) {
            throw new IOException("Ошибка при загрузке файла " + file, e);
        }
    }

    public static void save(String file, Object data) throws IOException {
        Path path = Path.of("src/json", file);

        try (FileWriter write = new FileWriter(path.toFile())) {
            getGson().toJson(data, write);
        }
    }
}
