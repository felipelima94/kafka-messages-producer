package com.felipedelima;

import com.felipedelima.configurations.AppConfig;
import com.felipedelima.services.KafkaProducerService;
import com.felipedelima.services.ReadTemplate;
import com.felipedelima.services.UUIDGen;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final ReadTemplate readTemplate = new ReadTemplate("demo-template.json");
    private static final KafkaProducerService kafkaProducerService = new KafkaProducerService();
    public static void main( String[] args )
    {
        Integer loop = readTemplate.getLoops();
        while (loop > 0) {
            int delay = getRandomBetween(readTemplate.getloopDeleyMin(), readTemplate.getloopDeleyMax());
            kafkaProducerService.sendMessage(AppConfig.getKafkaTopic(), UUIDGen.generateUUID(), getTemplate());
            System.out.println("Loop: " + loop + " - Delay: " + delay + "ms");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loop--;
        }
    }

    public static String getTemplate() {
        return readTemplate.getMessageTemplate("template").toString();
    }

    public static int getRandomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
