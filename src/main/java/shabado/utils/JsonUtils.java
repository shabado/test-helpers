package shabado.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.*;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (isArray(path)) {
            addValueToArrayAtPath(path, value, context);
        } else {
            context.set(path, value);
        }
        //Uses json().toString() rather than jsonString() to retain nulls.
        return context.json().toString();
    }

    private static void addValueToArrayAtPath(String path, JsonElement value, DocumentContext context) {
        String propertySubstring = path.substring(0, path.lastIndexOf('['));
        JsonArray array = context.read(propertySubstring);

        String arraySubstring = path.substring(path.lastIndexOf('['));
        String indexString = CharMatcher.anyOf("[]").trimFrom(arraySubstring);
        int index = Integer.parseInt(indexString);

        if (array.size() == 0 && index == 0 || array.size() == index) {
            array.add(value);
            context.set(propertySubstring, array);
        } else if (array.size() >= index) {
            context.set(path, value);
        } else {
            throw new RuntimeException("Cannot add element at index " + index +
                    " when array size is " + array.size());
        }
    }

    private static DocumentContext updateContext(String json, String originalPath, DocumentContext context) {
        //Add property to the context if not on the root object and property doesn't exist
        if (!originalPath.equals("$") && getAttributeValue(json, originalPath) == null) {
            String subPath = originalPath.substring(0, originalPath.lastIndexOf("."));
            String property = originalPath.substring(originalPath.lastIndexOf(".") + 1);
            updateContext(json, subPath, context);
            if (isArray(originalPath)) {
                updateContextArray(context, subPath, originalPath, property);
            } else {
                context.put(subPath, property, new JsonObject());
            }
        }
        return context;
    }

    private static void updateContextArray(DocumentContext context, String subPath,
                                           String originalPath, String property) {
        String arraySubstring = property.substring(property.lastIndexOf('['));
        String indexString = CharMatcher.anyOf("[]").trimFrom(arraySubstring);
        int index = Integer.parseInt(indexString);

        if (index == 0) {
            String propertySubstring = property.substring(0, property.lastIndexOf('['));
            context.put(subPath, propertySubstring, new JsonArray());
        } else {
            addValueToArrayAtPath(originalPath, new JsonObject(), context);
        }
    }

    private static boolean isArray(String property) {
        int index = property.lastIndexOf('[');
        if (index > -1) {
            String arraySubstring = property.substring(index);

            String patternString = "^[0-9\\[\\]]*$";

            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(arraySubstring);
            return matcher.matches();
        }
        return false;
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