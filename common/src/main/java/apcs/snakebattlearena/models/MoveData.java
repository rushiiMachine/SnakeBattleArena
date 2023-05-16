package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

@AutoValue
@JsonSerialize(as = MoveData.class)
@JsonDeserialize(builder = MoveData.Builder.class)
public abstract class MoveData {
    @NotNull
    @JsonProperty("direction")
    public abstract Direction getDirection();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum Direction {
        LEFT(-1, 0),
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1);

        /**
         * Positive or negative coordinate difference.
         */
        public final int x, y;

        Direction(int xDiff, int yDiff) {
            this.x = xDiff;
            this.y = yDiff;
        }
    }

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_MoveData.Builder();
        }

        @NotNull
        @JsonProperty("direction")
        public abstract Builder setDirection(@NotNull Direction direction);

        @NotNull
        public abstract MoveData build();
    }
}
