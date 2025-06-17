package com.felipedelima;

import com.felipedelima.configurations.AppConfig;
import com.felipedelima.services.KafkaProducerService;
import com.felipedelima.services.ReadTemplate;
import com.felipedelima.services.UUIDGen;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final ReadTemplate readTemplate = new ReadTemplate(AppConfig.getTempladeUri());
    private static final KafkaProducerService kafkaProducerService = new KafkaProducerService();
    public static void main( String[] args )
    {
        Integer loop = readTemplate.getLoops();
        System.out.printf("Seeding topic: %s\n", AppConfig.getKafkaTopic());
        while (loop > 0) {
            int delay = getRandomBetween(readTemplate.getloopDeleyMin(), readTemplate.getloopDeleyMax());
            kafkaProducerService.sendMessage(AppConfig.getKafkaTopic(), UUIDGen.generateUUID(), getTemplate());
            System.out.printf("Loop: %d - Delay: %dms\n", loop, delay);
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
