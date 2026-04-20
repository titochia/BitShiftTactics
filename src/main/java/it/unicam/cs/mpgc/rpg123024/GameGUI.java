package it.unicam.cs.mpgc.rpg123024;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Questa classe è la View principale del gioco.
 * Estendere "Application" la trasforma in una finestra JavaFX.
 */
public class GameGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Creiamo il "nodo" radice (un layout di base in cui metteremo la griglia)
        StackPane root = new StackPane();

        // Aggiungiamo un testo temporaneo al centro per assicurarci che funzioni
        root.getChildren().add(new Label("Interfaccia Grafica di Bit Shift Tactics Attiva!"));

        // 2. Creiamo la Scena (il contenuto della finestra) e diamo una risoluzione (es. 800x600)
        Scene scene = new Scene(root, 800, 600);

        // 3. Configuriamo la Finestra fisica (Stage)
        primaryStage.setTitle("Bit Shift Tactics");
        primaryStage.setScene(scene);

        // Blocchiamo il ridimensionamento così la griglia non si sforma se l'utente tira i bordi
        primaryStage.setResizable(false);

        // Mostriamo la finestra a schermo!
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Questo comando "accende" il motore grafico
        launch(args);
    }
}