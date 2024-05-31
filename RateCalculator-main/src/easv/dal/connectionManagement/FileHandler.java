package easv.dal.connectionManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    private final String dataBaseLoginPath = "src/easv/resources/DbLogin.txt";

    public String[] readDbLogin() {
        try {
            String dbCredentials = Files.readString(Path.of(dataBaseLoginPath));
            return dbCredentials.split(",");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
