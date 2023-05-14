package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@AutoValue
@JsonSerialize(as = SnakeData.class)
@JsonDeserialize(builder = SnakeData.Builder.class)
public abstract class SnakeData {
    @NotNull
    @JsonProperty("name")
    public abstract String getName();

    @NotNull
    @JsonProperty("color")
    public abstract Color getColor();

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @NotNull
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_SnakeData.Builder();
        }

        @NotNull
        @JsonProperty("name")
        public abstract Builder setName(String name);

        @NotNull
        @JsonProperty("color")
        public abstract Builder setColor(Color name);

        @NotNull
        public abstract SnakeData build();
    }
}
