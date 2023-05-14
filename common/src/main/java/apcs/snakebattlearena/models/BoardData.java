package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

@AutoValue
@JsonSerialize(as = BoardData.class)
@JsonDeserialize(builder = BoardData.Builder.class)
public abstract class BoardData {
    @JsonProperty("width")
    public abstract int getWidth();

    @JsonProperty("height")
    public abstract int getHeight();

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @NotNull
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_BoardData.Builder();
        }

        @NotNull
        @JsonProperty("width")
        public abstract Builder setWidth(int width);

        @NotNull
        @JsonProperty("height")
        public abstract Builder setHeight(int height);

        @NotNull
        public abstract BoardData build();
    }
}
