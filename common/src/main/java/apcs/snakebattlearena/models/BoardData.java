package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

/**
 * Basic board metadata sent when connecting.
 */
@AutoValue
@JsonSerialize(as = BoardData.class)
@JsonDeserialize(builder = BoardData.Builder.class)
public abstract class BoardData {
    /**
     * Static sized board width in squares.
     */
    @JsonProperty("width")
    public abstract int getWidth();

    /**
     * Static sized board height in squares.
     */
    @JsonProperty("height")
    public abstract int getHeight();

    @AutoValue.Builder
    public abstract static class Builder {
        @NotNull
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_BoardData.Builder();
        }

        @JsonProperty("width")
        public abstract Builder setWidth(int width);

        @JsonProperty("height")
        public abstract Builder setHeight(int height);

        public abstract BoardData build();
    }
}
