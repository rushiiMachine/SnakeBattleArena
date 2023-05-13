package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

@AutoValue
@JsonSerialize(as = Greeting.class)
public abstract class Greeting {
    @NotNull
    @JsonCreator
    public static Builder builder() {
        return new AutoValue_Greeting.Builder();
    }

    @NotNull
    @JsonProperty("message")
    public abstract String getMessage();

    @JsonProperty("id")
    public abstract int getId();

    @AutoValue.Builder
    public abstract static class Builder {
        @NotNull
        @JsonProperty("message")
        public abstract Builder setMessage(@NotNull String message);

        @NotNull
        @JsonProperty("id")
        public abstract Builder setId(int id);

        @NotNull
        public abstract Greeting build();
    }
}
