package shabado.payload;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.net.URL;

public class FilePayloadBuilder extends PayloadBuilder<FilePayloadBuilder> {

    FilePayloadBuilder(String payload) {
        super(payload);
    }

    /**
     * Takes the path of a file in the resources folder and adds it as a base payload.
     *
     * @param filePath
     */
    public static FilePayloadBuilder withBasePayloadPath(String filePath) {

        return new FilePayloadBuilder(verifyPayload(filePath));
    }

    private static String verifyPayload(String filePath) {
        String payload = "";
        try {
            URL url = Resources.getResource(filePath);
            payload = Resources.toString(url, Charsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to read file from given path", ex);
        }

        try {
            new Gson().fromJson(payload, JsonObject.class);
        } catch (JsonSyntaxException ex) {
            throw new RuntimeException("Supplied String is not valid Json", ex);
        }

        return payload;
    }
}
