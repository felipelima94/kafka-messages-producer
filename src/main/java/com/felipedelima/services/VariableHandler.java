package com.felipedelima.services;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VariableHandler {

    /*
     * "${{id:uuid}}" => varName: id; type: uuid
     * "${{time:timestamp}}" => varName: time; type: timestamp
     * "${{userName:string['Angelo','Emily','Maxine','Hailey']}}" => varName: username; type: list<String>
     * # Template:
     * {
     *  "id": "${{id:uuid}}",
     *  "user": "${{user:String}}",
     *  "user_name": "${{userName:string['Angelo','Hailey']}}"
     *  "details": {
     *      "time": "${{time:timestamp}}"
     *  }
     * }
     */

    private VariableHandler() {}
    
    public static JsonNode replaceVars(JsonNode template) {
        String templateStr = template.toString();
        List<Map<String, List<String>>> vars = catchVariables(templateStr);
        Set<String> fields = findField(templateStr);

        for (String field : fields) {
            Map<String, List<String>> var = vars.stream()
                .filter(m -> m.containsKey(field))
                .findFirst()
                .orElse(null);
            if (var != null) {
                String type = var.get(field).get(0);
                String replacement = "";
                if ("uuid".equalsIgnoreCase(type)) {
                    replacement = getUUID();
                } else if ("timestamp".equalsIgnoreCase(type)) {
                    replacement = getTimestamp().toString();
                } else if ("string".equalsIgnoreCase(type)) {
                    List<String> vals = stringToList(var.get(field).get(1));
                    replacement = getStringRandom(vals);
                } else {
                    replacement = "sample_" + type;
                }
                templateStr = templateStr.replace(field, replacement);
            }
        }

        return stringToJsonNode(templateStr);
    }
    private static List<Map<String, List<String>>> catchVariables(String template) {
        List<Map<String, List<String>>> variables = new ArrayList<>();
        Set<String> varsFields = findField(template);
        for (String field : varsFields) {
            variables.add(catchVars(field));
        }
        return variables;
    }

    private static Set<String> findField(String template) {
        Set<String> variables = new HashSet<>();
        Pattern pattern = Pattern.compile("\\\"\\$\\{\\{\\w+:\\w+(\\[.*\\])?}}\\\"");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String match = matcher.group();
            variables.add(match);
        }
        return variables;
    }

    private static Map<String, List<String>> catchVars(String key) {
        Map<String, List<String>> result = new HashMap<>();
        Pattern pattern = Pattern.compile("\\\"\\$\\{\\{(\\w+):(\\w*)(\\[.*\\])?}}\\\"");
        Matcher matcher = pattern.matcher(key);
        while (matcher.find()) {
            List<String> objFound = new ArrayList<>();
            String type = matcher.group(2);
            String values = matcher.group(3) != null ? matcher.group(3) : null;
            objFound.add(type);
            objFound.add(values);
            result.put(key, objFound);
        }
        return result;

    }

    private static String getTimestamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    private static String getUUID() {
        return "\""+java.util.UUID.randomUUID().toString()+"\"";
    }

    private static String getStringRandom(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(list.size());
        return "\""+list.get(randomIndex)+"\"";
    }

    public static JsonNode stringToJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> stringToList(String input) {
        if (input == null || input.isEmpty()) return List.of();
        // Remove brackets and single quotes, then split by comma
        return Arrays.stream(
                input.replaceAll("[\\[\\]'\\s]", "") // remove [, ], ', and whitespace
                    .split(","))
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }

    
}
