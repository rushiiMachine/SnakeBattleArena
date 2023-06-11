package apcs.snakebattlearena.models;

import apcs.snakebattlearena.models.entities.EntityData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * All the data sent every tick to all clients, informing them
 * to process it and return a new command for the next tick.
 */
@AutoValue
@JsonSerialize(as = TickData.class)
@JsonDeserialize(builder = TickData.Builder.class)
public abstract class TickData {
    /**
     * All the alive entities on the board.
     */
    @NotNull
    @JsonProperty("entities")
    public abstract List<EntityData> getEntities();

    /**
     * All the removed entities from the board in this tick.
     * This includes dead snakes, eaten apples, etc.
     */
    @NotNull
    @JsonProperty("removedEntities")
    public abstract List<EntityData> getRemovedEntities();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_TickData.Builder();
        }

        @JsonProperty("entities")
        public abstract Builder setEntities(@NotNull List<EntityData> entities);

        @JsonProperty("removedEntities")
        public abstract Builder setRemovedEntities(@NotNull List<EntityData> snakes);

        public abstract TickData build();
    }
}
