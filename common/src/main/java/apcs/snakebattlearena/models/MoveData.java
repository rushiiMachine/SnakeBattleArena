package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @NotNull
    @JsonProperty("snakeName")
    public abstract String getSnakeName();

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
        @JsonProperty("snakeName")
        public abstract Builder setSnakeName(@NotNull String name);

        @NotNull
        public abstract MoveData build();
    }
}
