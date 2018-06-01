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
    private String originalJson;
    private String toCompare;
    private JSONCompareMode compareMode = JSONCompareMode.NON_EXTENSIBLE;

    private JsonComparer(String originalJson, String toCompare) {
        this.originalJson = originalJson;
        this.toCompare = toCompare;
        ignoredAttributes = new String[]{};
    }

    public static JsonComparer compare(String originalJson, String toCompare) {
        return new JsonComparer(originalJson, toCompare);
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
            return JSONCompare.compareJSON(originalJson, toCompare, comparator);
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void assertJsonEquals() {
        removeIgnoredAttributes();
        try {
            JSONAssert.assertEquals(originalJson, toCompare, compareMode);
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void removeIgnoredAttributes() {
        for (String attribute : ignoredAttributes) {
            originalJson = JsonUtils.removeAttribute(originalJson, attribute);
            toCompare = JsonUtils.removeAttribute(toCompare, attribute);
        }
    }

}
