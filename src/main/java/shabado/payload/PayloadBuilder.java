package shabado.payload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import shabado.utils.JsonUtils;

public abstract class PayloadBuilder<T extends PayloadBuilder<T>> {

    private String payload;

    protected PayloadBuilder(String payload) {
        this.payload = payload;
    }

    public T withAttribute(String path, JsonElement value) {
        return withAttribute(path, new Gson().toJson(value));
    }

    public T withAttribute(String path, String value) {
        payload = JsonUtils.setAttribute(payload, path, value);
        return (T) this;
    }

    public T withoutAttribute(String path) {
        payload = JsonUtils.removeAttribute(payload, path);
        return (T) this;
    }

    public String buildAsString() {
        return payload;
    }

    public JsonObject buildAsJsonObject() {
        return new Gson().fromJson(payload, JsonObject.class);
    }

    public <C> C buildAsObject(Class<C> type) {
        return new Gson().fromJson(payload, type);
    }
}
