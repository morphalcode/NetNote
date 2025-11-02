package client.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LanguageUtils {

    public static String getSavedLanguage() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("client/src/main/java/client/Config.properties")) {
            properties.load(inputStream);
            return properties.getProperty("language", "en_US");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "en_US";
    }

    public static void saveLanguage(String language) {
        Properties properties = new Properties();
        properties.setProperty("language", language);
        try (FileOutputStream outputStream = new FileOutputStream("client/src/main/java/client/Config.properties")) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
