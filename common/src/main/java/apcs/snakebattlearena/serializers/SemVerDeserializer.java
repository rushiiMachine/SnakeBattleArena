package apcs.snakebattlearena.serializers;

import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * JSON deserializer for {@link SemVer}.
 */
public class SemVerDeserializer extends JsonDeserializer<SemVer> {
    @Override
    public SemVer deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return new SemVer(p.getValueAsString());
    }
}
