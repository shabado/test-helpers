package shabado.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

public class JsonUtils {

    private static final Gson gson = new Gson();
    private static final Configuration configuration = Configuration.builder()
            .jsonProvider(new GsonJsonProvider())
            .mappingProvider(new GsonMappingProvider())
            .build();

    /**
     * Sets or adds a String value on supplied path.
     *
     * @param json  Json to be altered.
     * @param path  Dot notation path of attribute.
     * @param value Desired String value.
     * @return String with attribute added.
     */
    public static String setAttribute(String json, String path, String value) {
        JsonElement valueAsGson = gson.fromJson(value, JsonElement.class);
        return setAttribute(json, path, valueAsGson);
    }

    /**
     * Sets or adds a Gson JsonElement value on supplied path.
     *
     * @param json  Json to be altered.
     * @param path  Dot notation path of attribute.
     * @param value Desired JsonElement value.
     * @return String with attribute added.
     */
    public static String setAttribute(String json, String path, JsonElement value) {
        DocumentContext context = JsonPath.using(configuration).parse(json);

        updateContext(json, "$." + path, context);

        context.set(path, value);
        return context.jsonString();
    }

    private static DocumentContext updateContext(String json, String originalPath, DocumentContext context) {
        //Add property to the context if not on the root object and property doesn't exist
        if (!originalPath.equals("$") && getAttributeValue(json, originalPath) == null) {
            String subPath = originalPath.substring(0, originalPath.lastIndexOf("."));
            String property = originalPath.substring(originalPath.lastIndexOf(".") + 1);
            updateContext(json, subPath, context);
            context.put(subPath, property, new JsonObject());
        }
        return context;
    }

    /**
     * Removes the attribute at the supplied path.
     *
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
     *
     * @param json Json to query.
     * @param path Dot notation path of attribute.
     * @return The found value as String, or null.
     */
    public static String getAttributeValue(String json, String path) {
        try {
            JsonElement element = JsonPath.using(configuration).parse(json).read(path, JsonElement.class);
            if (element instanceof JsonPrimitive) {
                return element.getAsString();
            } else {
                return element.toString();
            }
        } catch (PathNotFoundException ex) {
            return null;
        }
    }
}