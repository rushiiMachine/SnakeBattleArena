package apcs.snakebattlearena.server;

import apcs.snakebattlearena.models.Greeting;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class ApiController {
    private final AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "world") String name) {
        return Greeting.builder()
                .setMessage(String.format("Hello %s!", name))
                .setId(counter.incrementAndGet())
                .build();
    }

    @GetMapping(value = "/data-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> dataStream() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> i + 1 + "%")
                .take(100);
    }
}
