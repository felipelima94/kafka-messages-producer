package com.felipedelima;

import com.fasterxml.jackson.databind.JsonNode;
import com.felipedelima.configurations.AppConfig;
import com.felipedelima.services.KafkaProducerService;
import com.felipedelima.services.ReadTemplate;
import com.felipedelima.services.UUIDGen;
import com.felipedelima.services.VariableHandler;

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
        int loops = readTemplate.getLoops();
        int loopCurrent = 0;
        System.out.printf("Seeding topic: %s\n", AppConfig.getKafkaTopic());
        while (loops > loopCurrent || loops == 0) {
            int delay = getRandomBetween(readTemplate.getloopDeleyMin(), readTemplate.getloopDeleyMax());
            kafkaProducerService.sendMessage(AppConfig.getKafkaTopic(), UUIDGen.generateUUID(), getTemplate());
            System.out.printf("Loop: %d - Delay: %dms\n", loopCurrent+1, delay);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loopCurrent++;
        }
    }

    public static String getTemplate() {
        JsonNode template = readTemplate.getMessageTemplate("template");
        template = VariableHandler.replaceVars(template);
        return template.toString();
    }

    public static int getRandomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
