package apcs.snakebattlearena.models.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Apple;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

/**
 * Serialized JSON data for an apple.
 * This is used only for tick data on entities.
 * Refer to {@link Apple} for more information on the properties.
 */
@AutoValue
@JsonSerialize(as = AppleData.class)
@JsonDeserialize(builder = AppleData.Builder.class)
public abstract class AppleData extends EntityData {
    @NotNull
    @JsonProperty("position")
    public abstract Point getPosition();

    @JsonProperty("reward")
    public abstract int getReward();

    @JsonProperty("eaten")
    public abstract boolean getEaten();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_AppleData.Builder();
        }

        @JsonProperty("position")
        public abstract Builder setPosition(@NotNull Point position);

        @JsonProperty("reward")
        public abstract Builder setReward(int reward);

        @JsonProperty("eaten")
        public abstract Builder setEaten(boolean eaten);

        public abstract AppleData build();
    }
}
