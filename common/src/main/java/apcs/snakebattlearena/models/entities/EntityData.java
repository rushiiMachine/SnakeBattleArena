package apcs.snakebattlearena.models.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SnakeData.class, name = "SNAKE"),
        @JsonSubTypes.Type(value = AppleData.class, name = "APPLE"),
        @JsonSubTypes.Type(value = WallData.class, name = "WALL"),
})
public class EntityData {
}
