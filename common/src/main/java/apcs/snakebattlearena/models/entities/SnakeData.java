package apcs.snakebattlearena.models.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.DeathReason;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoValue
@JsonSerialize(as = SnakeData.class)
@JsonDeserialize(builder = SnakeData.Builder.class)
public abstract class SnakeData extends EntityData {
    @NotNull
    @JsonProperty("metadata")
    public abstract SnakeMetadata getMetadata();

    @NotNull
    @JsonProperty("body")
    public abstract List<Point> getBody();

    @NotNull
    @JsonProperty("head")
    public abstract Point getHead();

    @JsonProperty("curledLength")
    public abstract int getCurledLength();

    @Nullable
    @JsonProperty("deathReason")
    public abstract DeathReason getDeathReason();

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_SnakeData.Builder();
        }

        @JsonProperty("metadata")
        public abstract Builder setMetadata(@NotNull SnakeMetadata metadata);

        @JsonProperty("body")
        public abstract Builder setBody(@NotNull List<Point> body);

        @JsonProperty("head")
        public abstract Builder setHead(@NotNull Point head);

        @JsonProperty("curledLength")
        public abstract Builder setCurledLength(int length);

        @JsonProperty("deathReason")
        public abstract Builder setDeathReason(@Nullable DeathReason reason);

        public abstract SnakeData build();
    }
}
