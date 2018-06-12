package shabado.utils;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JsonComparerTest {

    @Test
    public void should_assert_true_when_equal() {
        String first = "{\"name\":\"Kate\",\"id\":1}";
        String second = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(first, second).compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_assert_false_when_different() {
        String first = "{\"name\":\"Kate\",\"id\":1}";
        String second = "{\"name\":\"Kate\",\"id\":2}";

        JSONCompareResult result =JsonComparer.compare(first, second).compareJson();

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
        String first = "{\"name\":\"John\",\"id\":1}";
        String second = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(first, second)
                .ignoredAttributes("name").compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_ignore_multiple_removed_attributes() {
        String first = "{\"name\":\"John\",\"id\":1, \"sex\":\"male\"}";
        String second = "{\"name\":\"Kate\",\"id\":1, \"sex\":\"female\"}";

        JSONCompareResult result =JsonComparer.compare(first, second)
                .ignoredAttributes("name", "sex").compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_ignore_order() {
        String first = "{\"id\":1,\"name\":\"Kate\"}";
        String second = "{\"name\":\"Kate\",\"id\":1}";

        JSONCompareResult result =JsonComparer.compare(first, second).compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_allow_extensible_when_configured() {
        String first = "{\"name\":\"John\",\"id\":1}";
        String second = "{\"name\":\"John\",\"id\":1, \"sex\":\"female\"}";

        JSONCompareResult result =JsonComparer.compare(first, second)
                .allowAdditionalAttributes().compareJson();

        assertTrue(result.passed());
    }

    @Test
    public void should_not_allow_extensible_by_default() {
        String first = "{\"name\":\"John\",\"id\":1}";
        String second = "{\"name\":\"John\",\"id\":1, \"age\":\"32\"}";

        JSONCompareResult result =JsonComparer.compare(first, second).compareJson();

        assertTrue(result.failed());
        assertTrue(result.getMessage().contains("Unexpected: age"));
    }


    @Test
    public void multiple_errors_should_all_be_returned() {
        String first = "{\"name\":\"Kate\",\"id\":1}";
        String second = "{\"name\":\"John\",\"id\":2}";

        JSONCompareResult result =JsonComparer.compare(first, second).compareJson();

        assertTrue(result.getMessage().contains("id\nExpected: 1\n     got: 2"));
        assertTrue(result.getMessage().contains("name\nExpected: Kate\n     got: John"));
        assertTrue(result.failed());
    }
}
