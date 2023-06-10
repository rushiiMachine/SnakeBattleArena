package apcs.snakebattlearena.models;

import apcs.snakebattlearena.models.entities.EntityData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoValue
@JsonSerialize(as = TickData.class)
@JsonDeserialize(builder = TickData.Builder.class)
public abstract class TickData {
    @NotNull
    @JsonProperty("entities")
    public abstract List<EntityData> getEntities();

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
