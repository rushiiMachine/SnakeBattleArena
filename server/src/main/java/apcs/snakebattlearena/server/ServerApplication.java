package apcs.snakebattlearena.server;

import apcs.snakebattlearena.server.serializers.ColorDeserializer;
import apcs.snakebattlearena.server.serializers.ColorSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.awt.*;

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
                .serializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }
}
