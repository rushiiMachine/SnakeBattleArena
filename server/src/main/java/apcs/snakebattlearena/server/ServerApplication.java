package apcs.snakebattlearena.server;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.serializers.*;
import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.awt.Color;

/**
 * The main entrypoint into the server that starts Spring Boot
 */
@SpringBootApplication
@SuppressWarnings("unused")
public class ServerApplication {
    /**
     * Start the API & background game service
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    // Register additional custom JSON serializers to be used
    @Bean
    public Jackson2ObjectMapperBuilder jacksonMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializerByType(Color.class, new ColorSerializer())
                .deserializerByType(Color.class, new ColorDeserializer())
                .serializerByType(SemVer.class, new SemVerSerializer())
                .deserializerByType(SemVer.class, new SemVerDeserializer())
                .serializerByType(Point.class, new PointSerializer())
                .deserializerByType(Point.class, new PointDeserializer())
                .serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }
}
