package apcs.snakebattlearena.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.awt.*;
import java.io.IOException;

/**
 * JSON deserializer for {@link Color} from integer RGB format.
 */
public class ColorDeserializer extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return new Color(p.getValueAsInt());
    }
}
