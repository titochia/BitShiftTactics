plugins {
    id("java")
    id("application") // Permette a Gradle di avviare la nostra app grafica
    id("org.openjfx.javafxplugin") version "0.0.13" // Il plugin ufficiale di JavaFX
}

group = "it.unicam.cs.mpgc.rpg123024"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configurazione di JavaFX
javafx {
    version = "21" // Usiamo la versione allineata al tuo Java 21
    modules = listOf("javafx.controls", "javafx.graphics")
}

// Diciamo a Gradle quale sarà il punto di partenza della grafica (creeremo questa classe tra poco!)
application {
    mainClass.set("it.unicam.cs.mpgc.rpg123024.GameGUI")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}