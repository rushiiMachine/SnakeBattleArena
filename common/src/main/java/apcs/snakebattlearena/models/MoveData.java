package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

// TODO: remove this instead in place for just the Direction
@AutoValue
@JsonSerialize(as = MoveData.class)
@JsonDeserialize(builder = MoveData.Builder.class)
public abstract class MoveData {
    @NotNull
    @JsonProperty("direction")
    public abstract Direction getDirection();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_MoveData.Builder();
        }

        @JsonProperty("direction")
        public abstract Builder setDirection(@NotNull Direction direction);

        public abstract MoveData build();
    }
}
