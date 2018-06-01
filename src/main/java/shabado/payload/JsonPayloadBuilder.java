package shabado.payload;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class JsonPayloadBuilder extends PayloadBuilder<JsonPayloadBuilder> {

    private JsonPayloadBuilder(String payload) {
        super(payload);
    }

    private static JsonPayloadBuilder withBasePayload(String basePayload) {
        try {
            new Gson().fromJson(basePayload, JsonObject.class);
        } catch (JsonSyntaxException ex) {
            throw new RuntimeException("Supplied String is not valid Json", ex);
        }
        return new JsonPayloadBuilder(basePayload);
    }
}
