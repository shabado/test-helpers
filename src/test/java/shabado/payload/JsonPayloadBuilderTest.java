package shabado.payload;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonPayloadBuilderTest {

    private static final String testPayload = "{\"name\":\"John\",\"id\":1, \"nestedObj\":{\"nestedAttribute\":123}}";

    @Test
    public void should_set_base_payload(){
        String payload = JsonPayloadBuilder.withBasePayload(testPayload).buildAsString();
        assertEquals(testPayload, payload);
    }

    @Test
    public void should_replace_value() {
        String payload = JsonPayloadBuilder.withBasePayload(testPayload)
                .withAttribute("nestedObj.nestedAttribute", "newValue").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_add_string_value() {
        String payload = JsonPayloadBuilder.withBasePayload(testPayload)
                .withAttribute("newObject.newProperty", "newValue").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":123}," +
                "\"newObject\":{\"newProperty\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_add_json_element() {
        JsonObject jsonObject = new Gson()
                .fromJson("{\"newProperty\":\"newValue\"}", JsonObject.class);

        String payload = JsonPayloadBuilder.withBasePayload(testPayload)
                .withAttribute("newObject", jsonObject).buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{\"nestedAttribute\":123}," +
                "\"newObject\":{\"newProperty\":\"newValue\"}}";

        assertEquals(expected, payload);
    }

    @Test
    public void should_remove_attribute() {
        String payload = JsonPayloadBuilder
                .withBasePayload(testPayload).withoutAttribute("nestedObj.nestedAttribute").buildAsString();

        String expected = "{\"name\":\"John\",\"id\":1,\"nestedObj\":{}}";
        assertEquals(expected, payload);
    }

    @Test
    public void should_return_as_JsonElement() {
        JsonObject actualObject = JsonPayloadBuilder.withBasePayload(testPayload)
                .buildAsJsonObject();

        JsonObject expectedObject = new Gson().fromJson(testPayload, JsonObject.class);

        assertEquals(expectedObject, actualObject);
    }

    @Test
    public void should_return_as_given_object() {
        TestObj expectedObj = new TestObj("testString", 123);
        String testObjAsString = "{\"testString\":\"testString\", \"testInt\":123}";

        TestObj actualObj = JsonPayloadBuilder.withBasePayload(testObjAsString).buildAsObject(TestObj.class);

        assertEquals(expectedObj, actualObj);
    }

    @Test
    public void should_throw_if_invalid_json() {
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> JsonPayloadBuilder.withBasePayload("notJson").buildAsString());

        assertEquals("Supplied String is not valid Json", error.getMessage());
    }
}
