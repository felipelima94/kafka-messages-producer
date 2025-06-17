package com.felipedelima.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ReadTemplate {

    private static JsonNode template;
    private static Integer loops = 1;
    private static Integer loopDelayMin = 0;
    private static Integer loopDelayMax = 0;

    static final ObjectMapper mapper = new ObjectMapper();

    public ReadTemplate(String filename) {
        template = readJson(filename);
        loadAppConfig();
    }
   
    public JsonNode getMessageTemplate(String key) {
        if (template != null && template.has(key)) {
            return template.get(key);
        } else {
            return null;
        }
    }

    public Integer getLoops() {
        return loops;
    }

    public Integer getloopDeleyMin() {
        return loopDelayMin;
    }
    
    public Integer getloopDeleyMax() {
        return loopDelayMax;
    }

    private static JsonNode readJson(String fileName) {
        try {
            return mapper.readTree(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
    }

    private static void loadAppConfig() {
        readLoops();
        readLoopsDelay();
    }

    private static JsonNode getAppRunningConfig() {
        if (template != null && template.has("appRunningConfig")) {
            return template.get("appRunningConfig");
        } else {
            return mapper.createObjectNode();
        }
    }

    private static void readLoops() {
        if (!getAppRunningConfig().isNull() && getAppRunningConfig().has("loops")) {
            loops = template.get("appRunningConfig").get("loops").asInt();
        }
    }

    private static void readLoopsDelay() {
        if (!getAppRunningConfig().isNull() && getAppRunningConfig().has("loopsDelay")) {
            if (getAppRunningConfig().get("loopsDelay").has("min")) {
                loopDelayMin = getAppRunningConfig().get("loopsDelay").get("min").asInt();
            } else {
                loopDelayMin = 0;
            }
            if (getAppRunningConfig().get("loopsDelay").has("max")) {
                loopDelayMax = getAppRunningConfig().get("loopsDelay").get("max").asInt();
            } else {
                loopDelayMax = 0;
            }
        }
    }
}
