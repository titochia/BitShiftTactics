package it.unicam.cs.mpgc.rpg123024;

import it.unicam.cs.mpgc.rpg123024.engine.TurnManager;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Interfaccia Grafica di Bit Shift Tactics con rendering vettoriale avanzato.
 * Stile: Firewall (Ottagono Blu), Virus (Stella Simmetrica Rossa).
 */
public class GameGUI extends Application {

    private static final int COLS = 8;
    private static final int ROWS = 5;
    private static final int CELL_SIZE = 80;

    private TurnManager engine;
    private Grid logicalGrid;
    private Label statsLabel;
    private GridPane gridPane;

    @Override
    public void start(Stage primaryStage) {
        logicalGrid = new Grid(COLS, ROWS);
        engine = new TurnManager(logicalGrid);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: BLACK;");

        // --- 1. HUD (Heads-Up Display) ---
        HBox hud = new HBox(20);
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(15));
        hud.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333; -fx-border-width: 0 0 2 0;");

        statsLabel = new Label();
        statsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        statsLabel.setTextFill(Color.WHITE);

        Button btnPassTurn = new Button("Passa Turno");
        btnPassTurn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
        btnPassTurn.setOnAction(e -> handleEndTurn());

        Button btnSpawnEnemy = new Button("Spawna Virus (Test)");
        btnSpawnEnemy.setStyle("-fx-font-weight: bold; -fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
        btnSpawnEnemy.setOnAction(e -> handleSpawnEnemy());

        hud.getChildren().addAll(statsLabel, btnPassTurn, btnSpawnEnemy);
        root.setTop(hud);

        // --- 2. GRIGLIA DI GIOCO ---
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = new StackPane();

                // Blindatura della dimensione
                cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
                cellPane.setMaxSize(CELL_SIZE, CELL_SIZE);

                Rectangle bg = new Rectangle(CELL_SIZE, CELL_SIZE);
                bg.setStroke(Color.BLACK);

                if (col == 0) bg.setFill(Color.DARKRED);
                else bg.setFill(Color.web("#2b2b2b"));

                cellPane.getChildren().add(bg);
                gridPane.add(cellPane, col, row);

                final int finalCol = col;
                final int finalRow = row;
                cellPane.setOnMouseClicked(event -> handleCellClick(finalCol, finalRow));
            }
        }

        root.setCenter(gridPane);

        Scene scene = new Scene(root, 800, 650);

        primaryStage.setTitle("Bit Shift Tactics");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        updateView();
    }

    // --- LOGICA DI INPUT ATTUALE (Solo Piazzamento) ---

    private void handleCellClick(int col, int row) {
        if (engine.placeFirewall("FW_" + col + "_" + row, col, row)) {
            updateView();
        }
    }

    private void handleEndTurn() {
        engine.endPlayerTurn();
        updateView();
    }

    private void handleSpawnEnemy() {
        int randomRow = (int) (Math.random() * ROWS);
        Virus v = new Virus("V_TEST", 7, randomRow, 100);
        engine.spawnVirus(v);
        updateView();
    }

    // --- AGGIORNAMENTO GRAFICO VETTORIALE ---

    private void updateView() {
        statsLabel.setText(String.format("CORE HP: %d  |  DATA SCRAPS: %d  |  STATO: %s",
                engine.getCoreHp(), engine.getDataScraps(), engine.getCurrentState()));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cellPane = getCellPaneAt(col, row);
                if (cellPane == null) continue;

                // PULIZIA: Rimuoviamo vecchie forme mantenendo lo sfondo
                cellPane.getChildren().removeIf(node -> node instanceof Shape && node != cellPane.getChildren().get(0));

                Optional<Entity> entityOpt = logicalGrid.getEntityAt(col, row);

                if (entityOpt.isPresent()) {
                    Entity e = entityOpt.get();
                    if (e instanceof Firewall) {
                        cellPane.getChildren().add(createFirewallShape());
                    } else if (e instanceof Virus) {
                        cellPane.getChildren().add(createVirusShape());
                    }
                }
            }
        }
    }

    private StackPane getCellPaneAt(int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (StackPane) node;
            }
        }
        return null;
    }

    // --- HELPER VISIVI DEFINITIVI ---

    // Crea un Ottagono Massiccio Blu (Sicurezza)
    private Polygon createFirewallShape() {
        Polygon firewall = new Polygon(
                25, 10,
                55, 10,
                70, 25,
                70, 55,
                55, 70,
                25, 70,
                10, 55,
                10, 25
        );
        firewall.setFill(Color.web("#1565C0")); // Blu profondo
        firewall.setStroke(Color.CYAN);         // Bordo scudo energetico
        firewall.setStrokeWidth(3);
        return firewall;
    }

    // Crea una Stella a 4 Punte Simmetrica Rossa (Pericolo/Malware)
    private Polygon createVirusShape() {
        Polygon virus = new Polygon(
                40, 15,  // Punta Nord
                48, 32,  // Angolo interno alto-destra
                65, 40,  // Punta Est
                48, 48,  // Angolo interno basso-destra
                40, 65,  // Punta Sud
                32, 48,  // Angolo interno basso-sinistra
                15, 40,  // Punta Ovest
                32, 32   // Angolo interno alto-sinistra
        );
        virus.setFill(Color.web("#8B0000"));   // Rosso scuro corrotto
        virus.setStroke(Color.web("#FF3333")); // Bordo rosso neon
        virus.setStrokeWidth(3);

        return virus;
    }

    public static void main(String[] args) {
        launch(args);
    }
}