package apcs.snakebattlearena.serializers;

import apcs.snakebattlearena.Point;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.*;
import java.io.IOException;

/**
 * JSON serializer for {@link Point} to a more efficient format. (X;Y)
 */
public class PointSerializer extends JsonSerializer<Point> {
    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeString(value.getX() + ";" + value.getY());
    }
}
