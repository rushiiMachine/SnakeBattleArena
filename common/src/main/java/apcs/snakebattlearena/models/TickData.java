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
    @JsonProperty("entities")
    public abstract List<EntityData> getEntities();

    @JsonProperty("removedEntities")
    public abstract List<EntityData> getRemovedEntities();

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @NotNull
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_TickData.Builder();
        }

        @NotNull
        @JsonProperty("entities")
        public abstract Builder setEntities(List<EntityData> entities);

        @NotNull
        @JsonProperty("removedEntities")
        public abstract Builder setRemovedEntities(List<EntityData> snakes);

        @NotNull
        public abstract TickData build();
    }
}
