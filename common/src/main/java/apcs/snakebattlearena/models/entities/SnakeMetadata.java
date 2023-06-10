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
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_SnakeMetadata.Builder();
        }

        @JsonProperty("name")
        public abstract Builder setName(@NotNull String name);

        @JsonProperty("color")
        public abstract Builder setColor(@NotNull Color name);

        public abstract SnakeMetadata build();
    }
}
