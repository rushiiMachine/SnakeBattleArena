plugins {
    id("org.springframework.boot") version "3.0.6"
}

val springVersion = "2.7.11" // Latest base spring-boot version that uses framework 5.3.x (Java 8+)

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-websocket:$springVersion")
    compileOnly("org.jetbrains:annotations:24.0.0")
}
