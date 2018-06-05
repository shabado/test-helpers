package shabado.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonUtilsTest {

    @Test
    public void should_add_new_attribute_with_String() throws Throwable {
        String initial = "{\"id\":1}";
        String expected = "{\"name\":\"John\",\"id\":1}";
        String updated = JsonUtils.setAttribute(initial, "name", "John");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_add_new_attribute_with_JsonElement() throws Throwable {
        String initial = "{\"id\":1}";
        String expected = "{\"id\":1, \"path\":{\"name\":\"John\"}}";
        JsonObject jsonOb = new JsonObject();
        jsonOb.addProperty("name", "John");

        String updated = JsonUtils.setAttribute(initial, "path", jsonOb);
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_update_attribute_with_String() throws Throwable {
        String initial = "{\"name\":\"John\",\"id\":1}";
        String expected = "{\"name\":\"Kate\",\"id\":1}";

        String updated = JsonUtils.setAttribute(initial, "name", "Kate");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_update_attribute_with_JsonElement() throws Throwable {
        String initial = "{\"name\":\"John\",\"id\":1}";
        String expected = "{\"name\":\"Kate\",\"id\":1}";

        JsonPrimitive jsonOb = new JsonPrimitive("Kate");

        String updated = JsonUtils.setAttribute(initial, "name", jsonOb);
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_get_attribute() {
        String json = "{\"name\":\"John\",\"id\":1}";

        String value = JsonUtils.getAttributeValue(json, "name");

        assertEquals("John", value);
    }

    @Test
    public void should_get_json_object_as_String() {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";
        String expected = "{\"name\":\"John\"}";

        String value = JsonUtils.getAttributeValue(initial, "path");

        assertEquals(expected, value);
    }

    @Test
    public void should_return_null_for_empty_attibute() {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";

        assertNull(JsonUtils.getAttributeValue(initial,"not.on.path"));
    }

    @Test
    public void should_remove_attribute() throws Throwable {
        String initial = "{\"name\":\"John\",\"id\":1}";
        String expected = "{\"id\":1}";
        String updated = JsonUtils.removeAttribute(initial, "name");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_add_attribute_with_dot_notation() throws Throwable {
        String initial = "{\"id\":1}";
        String expected = "{\"id\":1, \"path\":{\"name\":{\"Kate\":\"test\"}}}";
        String updated = JsonUtils.setAttribute(initial, "path.name", "{\"Kate\":\"test\"}");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_update_attribute_with_dot_notation() throws Throwable {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";
        String expected = "{\"id\":1, \"path\":{\"name\":{\"Kate\":\"object\"}}}";

        String updated = JsonUtils.setAttribute(initial, "path.name", "{\"Kate\":\"object\"}");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_retain_properties_when_adding_new() throws Throwable {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\", \"sex\":\"male\"}}";
        String expected = "{\"id\":1, \"path\":{\"name\":{\"Kate\":\"object\"}, \"sex\":\"male\"}}";

        String updated = JsonUtils.setAttribute(initial, "path.name", "{\"Kate\":\"object\"}");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void should_get_attribute_with_dot_notation() {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";

        String value = JsonUtils.getAttributeValue(initial, "path.name");
        assertEquals("John", value);
    }

    @Test
    public void should_remove_attribute_with_dot_notation() throws Throwable {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";
        String expected =  "{\"id\":1, \"path\":{}}";

        String updated = JsonUtils.removeAttribute(initial, "path.name");
        JSONAssert.assertEquals(expected, updated, JSONCompareMode.NON_EXTENSIBLE);
    }
}
