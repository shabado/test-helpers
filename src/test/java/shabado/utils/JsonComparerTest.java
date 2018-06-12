package shabado.utils;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class JsonComparerTest {

    @Test
    public void should_assert_true_when_equal() {
        String expected = "{\"name\":\"Kate\",\"id\":1}";
        String actual = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(expected, actual).compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_assert_false_when_different() {
        String expected = "{\"name\":\"Kate\",\"id\":1}";
        String actual = "{\"name\":\"Kate\",\"id\":2}";

        JSONCompareResult result =JsonComparer.compare(expected, actual).compareJson();

        assertTrue(result.getMessage().contains("id\nExpected: 1\n     got: 2"));
        assertTrue(result.failed());
    }

    @Test
    public void should_throw_if_input_not_json() {
        assertThrows(RuntimeException.class,
                () -> JsonComparer.compare("notJson", "notJson").compareJson());

        assertThrows(RuntimeException.class,
                () -> JsonComparer.compare("notJson", "{}").compareJson());

        assertThrows(RuntimeException.class,
                () -> JsonComparer.compare("{}", "notJson").compareJson());
    }

    @Test
    public void should_not_compare_removed_attributes() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(expected, actual)
                .ignoredAttributes("name").compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_ignore_multiple_removed_attributes() {
        String expected = "{\"name\":\"John\",\"id\":1, \"sex\":\"male\"}";
        String actual = "{\"name\":\"Kate\",\"id\":1, \"sex\":\"female\"}";

        JSONCompareResult result =JsonComparer.compare(expected, actual)
                .ignoredAttributes("name", "sex").compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_ignore_order() {
        String expected = "{\"id\":1,\"name\":\"Kate\"}";
        String actual = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(expected, actual).compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_allow_extensible_when_configured() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"John\",\"id\":1, \"sex\":\"female\"}";

        JSONCompareResult result =JsonComparer.compare(expected, actual)
                .allowAdditionalAttributes().compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_not_allow_extensible_by_default() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"John\",\"id\":1, \"age\":\"32\"}";

        JSONCompareResult result =JsonComparer.compare(expected, actual).compareJson();

        assertTrue(result.failed());
        assertTrue(result.getMessage().contains("Unexpected: age"));
    }


    @Test
    public void multiple_errors_should_all_be_returned() {
        String expected = "{\"name\":\"Kate\",\"id\":1}";
        String actual = "{\"name\":\"John\",\"id\":2}";

        JSONCompareResult result =JsonComparer.compare(expected, actual).compareJson();

        assertTrue(result.getMessage().contains("id\nExpected: 1\n     got: 2"));
        assertTrue(result.getMessage().contains("name\nExpected: Kate\n     got: John"));
        assertTrue(result.failed());
    }


    @Test
    public void should_pass_assertion_when_equal() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"John\",\"id\":1}";

        JsonComparer.compare(expected, actual).assertJsonEquals();
    }

    @Test
    public void should_pass_assertion_when_equal_when_attributes_ignored() {
        String expected = "{\"name\":\"John\",\"id\":1, \"ignore\":123}";
        String actual = "{\"name\":\"John\",\"id\":1}";

        JsonComparer.compare(expected, actual).ignoredAttributes("ignore").assertJsonEquals();
    }

    @Test
    public void should_pass_assertion_when_equal_when_additional_allowed() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"John\",\"id\":1, \"additional\":123}";

        JsonComparer.compare(expected, actual).allowAdditionalAttributes().assertJsonEquals();
    }

    @Test
    public void should_fail_assertion_when_different() {
        String expected = "{\"name\":\"John\",\"id\":1}";
        String actual = "{\"name\":\"Kate\",\"id\":1}";

        AssertionError error = assertThrows(AssertionError.class,
                () -> JsonComparer.compare(expected, actual).assertJsonEquals());

        assertTrue(error.getMessage().contains("name\nExpected: John\n     got: Kate"));
    }
}
