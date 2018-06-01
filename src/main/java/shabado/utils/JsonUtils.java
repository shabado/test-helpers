package shabado.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtils {

    private static final Gson gson = new Gson();
    private static final Configuration configuration = Configuration.builder()
            .jsonProvider(new GsonJsonProvider())
            .mappingProvider(new GsonMappingProvider())
            .build();

    /**
     * Sets or adds a String value on supplied path.
     * @param json Json to be altered.
     * @param path Dot notation path of attribute.
     * @param value Desired String value.
     * @return String with attribute added.
     */
    public static String setAttribute(String json, String path, String value) {
        JsonElement valueAsGson = gson.fromJson(value, JsonElement.class);
        return setAttribute(json, path, valueAsGson);
    }

    /**
     * Sets or adds a Gson JsonElement value on supplied path.
     * @param json Json to be altered.
     * @param path Dot notation path of attribute.
     * @param value Desired JsonElement value.
     * @return String with attribute added.
     */
    public static String setAttribute(String json, String path, JsonElement value) {
        return (getAttributeValue(json, path) == null) ? putAttribute(json, path, value)
                : updateAttribute(json, path, value);
    }

    private static String putAttribute(String json, String path, JsonElement value) {
        List<String> keys = Arrays.asList(path.split("\\."));
        DocumentContext context = JsonPath.using(configuration).parse(json);

        if (keys.size() == 1) {
            context.put("$", keys.get(0), value);
        } else {
            String attributeName = keys.get(keys.size() - 1);
            String newPath = String.join(".",
                    keys.stream().filter(s -> !s.equals(attributeName)).collect(Collectors.toList()));

            context.put(newPath, attributeName, value);
        }
        return context.jsonString();
    }

    private static String updateAttribute(String json, String path, JsonElement value) {
        DocumentContext context = JsonPath.using(configuration).parse(json);
        return context.set(path, value).jsonString();
    }

    /**
     * Removes the attribute at the supplied path.
     * @param json Json to be altered.
     * @param path Dot notation path of attribute.
     * @return Json with attribute removed.
     */
    public static String removeAttribute(String json, String path) {
        DocumentContext jsonContext = JsonPath.using(configuration).parse(json);
        return jsonContext.delete(path).jsonString();
    }

    /**
     * Gets the attribute value at the supplied path. Returns null if value is not found.
     * @param json Json to query.
     * @param path Dot notation path of attribute.
     * @return The found value as String, or null.
     */
    public static String getAttributeValue(String json, String path) {
        try {
            return JsonPath.using(configuration).parse(json).read(path).toString();
        } catch (PathNotFoundException ex) {
            return null;
        }

    }
}