package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoValue
@JsonSerialize(as = GameData.class)
@JsonDeserialize(builder = GameData.Builder.class)
public abstract class GameData {
    @JsonProperty("snakes")
    public abstract List<SnakeData> getSnakes();

    @JsonProperty("board")
    public abstract BoardData getBoard();

    @AutoValue.Builder
    @SuppressWarnings("NullableProblems")
    public abstract static class Builder {
        @NotNull
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_GameData.Builder();
        }

        @NotNull
        @JsonProperty("snakes")
        public abstract Builder setSnakes(List<SnakeData> snakes);

        @NotNull
        @JsonProperty("board")
        public abstract Builder setBoard(BoardData board);

        @NotNull
        public abstract GameData build();
    }
}
