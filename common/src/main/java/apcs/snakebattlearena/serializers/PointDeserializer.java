package apcs.snakebattlearena.serializers;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * JSON deserializer for {@link Point} in a more efficient format. (X;Y)
 */
public class PointDeserializer extends JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getValueAsString();

        String[] parts = value.split(";");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid serialized point coordinate!");
        }

        return new Point(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
        );
    }
}
