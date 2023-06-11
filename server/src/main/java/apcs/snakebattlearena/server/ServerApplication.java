package apcs.snakebattlearena.server;

import apcs.snakebattlearena.serializers.ColorDeserializer;
import apcs.snakebattlearena.serializers.ColorSerializer;
import apcs.snakebattlearena.serializers.SemVerDeserializer;
import apcs.snakebattlearena.serializers.SemVerSerializer;
import apcs.snakebattlearena.util.SemVer;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.awt.*;

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
                .serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }
}
