subprojects {
    apply(plugin = "java")

    group = "apcs.snakebattlearena"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    extensions.getByName<JavaPluginExtension>("java").apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
