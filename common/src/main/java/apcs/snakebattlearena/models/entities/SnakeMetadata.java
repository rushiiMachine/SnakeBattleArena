package apcs.snakebattlearena.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@AutoValue
@JsonSerialize(as = SnakeMetadata.class)
@JsonDeserialize(builder = SnakeMetadata.Builder.class)
public abstract class SnakeMetadata {
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
            return new AutoValue_SnakeMetadata.Builder();
        }

        @NotNull
        @JsonProperty("name")
        public abstract Builder setName(String name);

        @NotNull
        @JsonProperty("color")
        public abstract Builder setColor(Color name);

        @NotNull
        public abstract SnakeMetadata build();
    }
}
