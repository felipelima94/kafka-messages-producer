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
        String osEnv = System.getenv("KAFKA_BROKER");
        return osEnv == null ? yamlProps.get("kafka-broker").toString() : osEnv;
    }

    public static String getKafkaTopic() {
        String osEnv = System.getenv("KAFKA_TOPIC");
        return osEnv == null ? yamlProps.get("kafka-topic").toString() : osEnv;
    }

    public static String getTempladeUri() {
        String osEnv = System.getenv("TEMPLATE_URI");
        return osEnv == null ? yamlProps.get("template-uri").toString() : osEnv;
    }
}
