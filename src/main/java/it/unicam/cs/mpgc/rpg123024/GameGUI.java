package it.unicam.cs.mpgc.rpg123024;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameGUI extends Application {

    // Definiamo le dimensioni del nostro tabellone
    private static final int COLS = 8;
    private static final int ROWS = 5;
    private static final int CELL_SIZE = 80; // Ogni quadratino sarà 80x80 pixel

    @Override
    public void start(Stage primaryStage) {
        // 1. Creiamo il layout a griglia (GridPane) e lo centriamo nello schermo
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        // 2. Doppio ciclo For per costruire le 40 celle (8x5)
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                // Creiamo la forma geometrica della singola cella
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

                // Disegniamo il bordo nero attorno alla cella
                cell.setStroke(Color.BLACK);

                // 3. LA RED ZONE: Se siamo nella prima colonna (0), coloriamo di rosso!
                if (col == 0) {
                    cell.setFill(Color.DARKRED);
                } else {
                    // Altrimenti usiamo un colore grigio scuro stile "cyberpunk"
                    cell.setFill(Color.web("#2b2b2b"));
                }

                // 4. Aggiungiamo il quadratino appena creato alla griglia visiva
                // Attenzione: JavaFX vuole prima la colonna (x) e poi la riga (y)
                gridPane.add(cell, col, row);
            }
        }

        // 5. Impostiamo la scena, mettendo la griglia su uno sfondo nero
        Scene scene = new Scene(gridPane, 800, 600);
        scene.setFill(Color.BLACK);

        primaryStage.setTitle("Bit Shift Tactics - Battle Grid");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}