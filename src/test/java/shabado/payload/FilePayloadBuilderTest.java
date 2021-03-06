package shabado.payload;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilePayloadBuilderTest {

    private static final String filePath = "payloadfile.json";

    @Test
    public void should_create_payload_from_file() throws Throwable {
        String expected = "{\"name\":\"John\",\"id\":1, \"nestedObj\":{\"nestedAttribute\":123}}";

        String actual = FilePayloadBuilder.withBasePayloadPath(filePath).buildAsString();

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void should_replace_value() {
        String payload = FilePayloadBuilder.withBasePayloadPath(filePath)
                .withAttribute("nestedObj.nestedAttribute", "newValue").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_add_string_value() {
        String payload = FilePayloadBuilder.withBasePayloadPath(filePath)
                .withAttribute("newObject.newProperty", "newValue").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":123}," +
                "\"newObject\":{\"newProperty\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_add_json_element() {
        JsonObject jsonObject = new Gson()
                .fromJson("{\"newProperty\":\"newValue\"}", JsonObject.class);

        String payload = FilePayloadBuilder.withBasePayloadPath(filePath)
                .withAttribute("newObject", jsonObject).buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":123}," +
                "\"newObject\":{\"newProperty\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_remove_attribute() {
        String payload = FilePayloadBuilder
                .withBasePayloadPath(filePath).withoutAttribute("nestedObj.nestedAttribute").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{}}";
        assertEquals(expected, payload);
    }

    @Test
    public void should_return_as_JsonElement() {
        String expectedJson = "{\"name\":\"John\",\"id\":1, \"nestedObj\":{\"nestedAttribute\":123}}";

        JsonObject actualObject = FilePayloadBuilder.withBasePayloadPath(filePath)
                .buildAsJsonObject();

        JsonObject expectedObject = new Gson().fromJson(expectedJson, JsonObject.class);

        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void should_return_as_given_object() {
        TestObj expectedObj = new TestObj("testString", 123);

        TestObj actualObj = FilePayloadBuilder
                .withBasePayloadPath("testObj.json").buildAsObject(TestObj.class);

        assertEquals(expectedObj, actualObj);
    }

    @Test
    public void should_throw_if_file_not_found() {
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> FilePayloadBuilder.withBasePayloadPath("noFile").buildAsString());

        assertEquals("Unable to read file from given path", error.getMessage());
    }

    @Test
    public void should_throw_if_invalid_json() {
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> FilePayloadBuilder.withBasePayloadPath("notJsonFile.json").buildAsString());

        assertEquals("Supplied String is not valid Json", error.getMessage());
    }
}
