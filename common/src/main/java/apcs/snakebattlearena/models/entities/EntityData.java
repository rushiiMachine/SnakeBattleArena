package apcs.snakebattlearena.models.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AutoValue_SnakeData.class, name = "SNAKE"),
        @JsonSubTypes.Type(value = AutoValue_AppleData.class, name = "APPLE"),
        @JsonSubTypes.Type(value = AutoValue_WallData.class, name = "WALL"),
})
public class EntityData {
}
