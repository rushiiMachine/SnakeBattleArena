package apcs.snakebattlearena.models.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Wall;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

/**
 * Serialized JSON data for a wall.
 * This is only used for tick data on entities.
 * Refer to {@link Wall} for more information on the properties.
 */
@AutoValue
@JsonSerialize(as = WallData.class)
@JsonDeserialize(builder = WallData.Builder.class)
public abstract class WallData extends EntityData {
    @NotNull
    @JsonProperty("start")
    public abstract Point getStart();

    @JsonProperty("end")
    public abstract Point getEnd();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_WallData.Builder();
        }

        @JsonProperty("start")
        public abstract Builder setStart(@NotNull Point position);

        @JsonProperty("end")
        public abstract Builder setEnd(@NotNull Point position);

        public abstract WallData build();
    }
}
