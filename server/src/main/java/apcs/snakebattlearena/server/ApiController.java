package apcs.snakebattlearena.server;

import apcs.snakebattlearena.models.MoveData;
import apcs.snakebattlearena.models.entities.SnakeMetadata;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class ApiController {
    @GetMapping("/snake")
    public SnakeMetadata newSnake() {
        return SnakeMetadata.Builder.builder()
                .setName("rusherv2")
                .setColor(Color.PINK)
                .build();
    }

    @PostMapping("/move")
    public MoveData move(@RequestBody MoveData data) {
        return data;
    }
}
