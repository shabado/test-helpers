package shabado.utils;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

public class JsonComparer {

    private String[] ignoredAttributes;
    private String expectedJson;
    private String actualJson;
    private JSONCompareMode compareMode = JSONCompareMode.NON_EXTENSIBLE;

    private JsonComparer(String expectedJson, String actualJson) {
        this.expectedJson = expectedJson;
        this.actualJson = actualJson;
        ignoredAttributes = new String[]{};
    }

    public static JsonComparer compare(String expectedJson, String actualJson) {
        return new JsonComparer(expectedJson, actualJson);
    }

    /**
     * Excludes the provided attributes from the comparison.
     * @param attributes Comma separated list of dot notation Json attributes.
     */
    public JsonComparer ignoredAttributes(String... attributes) {
        ignoredAttributes = attributes;
        return this;
    }

    /**
     * Causes the comparison to take account of only those attributes present in both Json.
     * i.e. {name:"John", id:1} will match {name:"John"}.
     */
    public JsonComparer allowAdditionalAttributes() {
        compareMode = JSONCompareMode.LENIENT;
        return this;
    }

    public JSONCompareResult compareJson() {
        removeIgnoredAttributes();
        JSONComparator comparator = new DefaultComparator(compareMode);
        try {
            return JSONCompare.compareJSON(expectedJson, actualJson, comparator);
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void assertJsonEquals() {
        removeIgnoredAttributes();
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, compareMode);
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void removeIgnoredAttributes() {
        for (String attribute : ignoredAttributes) {
            expectedJson = JsonUtils.removeAttribute(expectedJson, attribute);
            actualJson = JsonUtils.removeAttribute(actualJson, attribute);
        }
    }

}
