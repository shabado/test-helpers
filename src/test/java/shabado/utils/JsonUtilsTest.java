package shabado.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {

    @Test
    public void should_add_new_attribute_with_String(){}

    @Test
    public void should_add_new_attribute_with_JsonElement(){}

    @Test
    public void should_update_attribute_with_String(){}

    @Test
    public void should_update_attribute_with_JsonElement(){}

    @Test
    public void should_get_attribute(){}

    @Test
    public void should_return_null_for_empty_attibute(){}

    @Test
    public void should_remove_attribute() {
        String initial = "{\"name\":\"John\",\"id\":1}";
        String expected = "{\"id\":1}";
        String updated = JsonUtils.removeAttribute(initial,"name");
        assertEquals(expected, updated);
    }

    @Test
    public void should_add_attribute_with_dot_notation(){}

    @Test
    public void should_update_attribute_with_dot_notation(){}

    @Test
    public void should_get_attribute_with_dot_notation(){}

    @Test
    public void should_remove_attribute_with_dot_notation(){}
}
