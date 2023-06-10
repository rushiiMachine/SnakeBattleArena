package apcs.snakebattlearena.serializers;

import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * JSON serializer for {@link SemVer}.
 */
public class SemVerSerializer extends JsonSerializer<SemVer> {
    @Override
    public void serialize(SemVer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeString(value.toString());
    }
}
