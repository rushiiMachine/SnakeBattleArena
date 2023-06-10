package apcs.snakebattlearena.models;

import apcs.snakebattlearena.models.entities.SnakeMetadata;
import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

@AutoValue
@JsonSerialize(as = JoinData.class)
@JsonDeserialize(builder = JoinData.Builder.class)
public abstract class JoinData {
    @NotNull
    @JsonProperty("snake")
    public abstract SnakeMetadata getSnake();

    @NotNull
    @JsonProperty("version")
    public abstract SemVer getClientVersion();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static @NotNull Builder builder() {
            return new AutoValue_JoinData.Builder();
        }

        @JsonProperty("snake")
        public abstract Builder setSnake(@NotNull SnakeMetadata snake);

        @JsonProperty("version")
        public abstract Builder setClientVersion(@NotNull SemVer version);

        public abstract JoinData build();
    }
}
