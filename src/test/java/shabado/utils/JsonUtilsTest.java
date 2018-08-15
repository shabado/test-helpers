package shabado.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.jupiter.api.Assertions.*;

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
    public void should_get_array_value() {
        String initial = "{\"id\":1,\"path\":[\"stringValue\"]}";
        String expected = "stringValue";

        String value = JsonUtils.getAttributeValue(initial, "path[0]");

        assertEquals(expected, value);
    }

    @Test
    public void should_set_array_value_when_adding_array() {
        String initial = "{\"id\":1}";
        String expected = "{\"id\":1,\"path\":[\"newString\"]}";

        String value = JsonUtils.setAttribute(initial, "path[0]", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_set_when_adding_to_array() {
        String initial = "{\"id\":1,\"path\":[\"oldString\"]}";
        String expected = "{\"id\":1,\"path\":[\"oldString\",\"newString\"]}";

        String value = JsonUtils.setAttribute(initial, "path[1]", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_set_array_value_overwriting() {
        String initial = "{\"id\":1,\"path\":[\"oldString\"]}";
        String expected = "{\"id\":1,\"path\":[\"newString\"]}";

        String value = JsonUtils.setAttribute(initial, "path[0]", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_set_item_in_middle_of_array() {
        String initial = "{\"id\":1,\"path\":[\"valueOne\",\"valueTwo\",\"valueThree\"]}";
        String expected = "{\"id\":1,\"path\":[\"valueOne\",\"newString\",\"valueThree\"]}";

        String value = JsonUtils.setAttribute(initial, "path[1]", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_throw_attempt_to_set_would_cause_missing_index() {
        String initial = "{\"id\":1,\"path\":[\"oldString\"]}";

        String error = assertThrows(RuntimeException.class,
                () -> JsonUtils.setAttribute(initial, "path[3]", "newString")).getMessage();

        String expectedError = "Cannot add element at index 3 when array size is 1";
        assertEquals(expectedError, error);
    }

    @Test
    public void should_overwrite_nested_property_in_array() {
        String initial = "{\"id\":1,\"path\":[{\"someValue\":\"oldString\"}]}";
        String expected = "{\"id\":1,\"path\":[{\"someValue\":\"newString\"}]}";

        String value = JsonUtils.setAttribute(initial, "path[0].someValue", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_set_nested_property_in_array() {
        String initial = "{\"id\":1}";
        String expected = "{\"id\":1,\"path\":[{\"prop\":\"newString\"}]}";

        String value = JsonUtils.setAttribute(initial, "path[0]", "{\"prop\":\"newString\"}");

        assertEquals(expected, value);
    }

    @Test
    public void should_get_nested_property_in_array() {
        String json = "{\"id\":1,\"path\":[{\"prop\":\"newString\"}]}";
        String value = JsonUtils.getAttributeValue(json, "path[0].prop");

        assertEquals("newString", value);
    }

    @Test
    public void should_add_to_array_with_nested_value() {
        String initial = "{\"id\":1,\"path\":[{\"someValue\":\"oldString\"}]}";
        String expected = "{\"id\":1,\"path\":[{\"someValue\":\"oldString\"},{\"someProp\":\"newString\"}]}";

        String value = JsonUtils.setAttribute(initial, "path[1].someProp", "newString");

        assertEquals(expected, value);
    }

    @Test
    public void should_throw_if_adding_to_nested_with_missing_index() {
        String initial = "{\"id\":1,\"path\":[{\"someValue\":\"oldString\"}]}";

        String error = assertThrows(RuntimeException.class,
                () -> JsonUtils.setAttribute(initial, "path[2].someProp", "newString")).getMessage();

        String expectedError = "Cannot add element at index 2 when array size is 1";
        assertEquals(expectedError, error);
    }

    @Test
    public void should_return_null_for_empty_attibute() {
        String initial = "{\"id\":1, \"path\":{\"name\":\"John\"}}";

        assertNull(JsonUtils.getAttributeValue(initial,"not.on.path"));
    }

    @Test
    public void should_set_attribute_to_null() throws Throwable {
        String initial = "{\"id\":1, \"name\":\"John\"}";
        String expected = "{\"id\":1, \"name\":null}";

        String nullValue = null;

        String actual = JsonUtils.setAttribute(initial, "name", nullValue);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);

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
