package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@AutoValue
@JsonSerialize(as = JoinResponseData.class)
@JsonDeserialize(builder = JoinResponseData.Builder.class)
public abstract class JoinResponseData {
    @NotNull
    @JsonProperty("error")
    public abstract Optional<JoinError> getError();

    @NotNull
    @JsonProperty("board")
    public abstract Optional<BoardData> getBoard();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder builder() {
            return new AutoValue_JoinResponseData.Builder();
        }

        @JsonProperty("error")
        public abstract Builder setError(@Nullable JoinError error);

        @JsonProperty("board")
        public abstract Builder setBoard(@Nullable BoardData board);

        public abstract JoinResponseData build();
    }
}
