package shabado.payload;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.net.URL;

public class FilePayloadBuilder extends PayloadBuilder<FilePayloadBuilder> {

    private FilePayloadBuilder(String payload) {
        super(payload);
    }

    /**
     * Takes the path of a file in the resources folder and adds it as a base payload.
     * @param filePath
     */
    private static FilePayloadBuilder withBasePayloadPath(String filePath) {
        URL url = Resources.getResource(filePath);
        try {
            String payload = Resources.toString(url, Charsets.UTF_8);
            return new FilePayloadBuilder(payload);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to read file from given path", ex);
        }
    }
}
