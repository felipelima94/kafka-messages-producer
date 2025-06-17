package com.felipedelima.configurations;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class AppConfig {
    private static Map<String, Object> yamlProps;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.yml")) {
            yamlProps = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getKafkaServer() {
        return (String) yamlProps.get("kafka-server");
    }

    public static String getKafkaTopic() {
        return (String) yamlProps.get("kafka-topic");
    }
}
