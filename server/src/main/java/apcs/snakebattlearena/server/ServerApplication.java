package apcs.snakebattlearena.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {
    /**
     * Start the API & background game service
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
