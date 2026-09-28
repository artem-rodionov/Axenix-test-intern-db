package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class ConfigManager {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    public static Properties getConfig() {
        Properties properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл db.properties не найден в classpath!");
            }

            properties.load(input);

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");

            log.info("URL: {}", url);
            log.info("Username: {}", username);


        } catch (IOException e) {
            log.error("Ошибка при чтении файла конфигурации", e);
            throw new RuntimeException("Не удалось загрузить db.properties", e);
        }

         return properties;
    }
}
