package it.unicam.cs.mpgc.rpg123024;

import it.unicam.cs.mpgc.rpg123024.engine.TurnManager;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class GameGUI extends Application {

    private static final int COLS = 8;
    private static final int ROWS = 5;
    private static final int CELL_SIZE = 80;

    private TurnManager engine;
    private Grid logicalGrid;
    private final Label[][] cellLabels = new Label[COLS][ROWS];

    // Nuovi elementi per l'HUD
    private Label statsLabel;

    @Override
    public void start(Stage primaryStage) {
        logicalGrid = new Grid(COLS, ROWS);
        engine = new TurnManager(logicalGrid);

        // IL NUOVO LAYOUT PRINCIPALE
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: BLACK;");

        // --- 1. CREAZIONE DELL'HUD (In alto) ---
        HBox hud = new HBox(20); // Spazio di 20px tra gli elementi
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(15));
        hud.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333; -fx-border-width: 0 0 2 0;");

        statsLabel = new Label();
        statsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        statsLabel.setTextFill(Color.WHITE);

        Button btnPassTurn = new Button("Passa Turno");
        btnPassTurn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnPassTurn.setOnAction(e -> handleEndTurn());

        Button btnSpawnEnemy = new Button("Spawna Virus (Test)");
        btnSpawnEnemy.setStyle("-fx-font-weight: bold; -fx-background-color: #f44336; -fx-text-fill: white;");
        btnSpawnEnemy.setOnAction(e -> handleSpawnEnemy());

        hud.getChildren().addAll(statsLabel, btnPassTurn, btnSpawnEnemy);
        root.setTop(hud); // Mettiamo l'HUD in cima

        // --- 2. CREAZIONE DELLA GRIGLIA (Al centro) ---
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = new StackPane();

                Rectangle bg = new Rectangle(CELL_SIZE, CELL_SIZE);
                bg.setStroke(Color.BLACK);
                if (col == 0) bg.setFill(Color.DARKRED);
                else bg.setFill(Color.web("#2b2b2b"));

                Label label = new Label("");
                label.setFont(Font.font("Arial", FontWeight.BOLD, 40));
                cellLabels[col][row] = label;

                cellPane.getChildren().addAll(bg, label);
                gridPane.add(cellPane, col, row);

                final int finalCol = col;
                final int finalRow = row;
                cellPane.setOnMouseClicked(event -> handleCellClick(finalCol, finalRow));
            }
        }

        // Mettiamo la griglia al centro del BorderPane
        root.setCenter(gridPane);

        // Aumentiamo un po' l'altezza della finestra per far spazio all'HUD
        Scene scene = new Scene(root, 800, 650);

        primaryStage.setTitle("Bit Shift Tactics");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        updateView();
    }

    // --- METODI DI INTERAZIONE ---

    private void handleCellClick(int col, int row) {
        if (engine.placeFirewall("FW_" + col + "_" + row, col, row)) {
            updateView();
        }
    }

    private void handleEndTurn() {
        engine.endPlayerTurn(); // Passa il turno e fa muovere i nemici!
        updateView();
    }

    private void handleSpawnEnemy() {
        // Crea un virus a caso nell'ultima colonna di destra
        int randomRow = (int) (Math.random() * ROWS);
        Virus v = new Virus("V_TEST", 7, randomRow, 100);
        engine.spawnVirus(v);
        updateView();
    }

    // --- AGGIORNAMENTO GRAFICO ---

    private void updateView() {
        // 1. Aggiorna i testi dell'HUD
        statsLabel.setText(String.format("CORE HP: %d  |  DATA SCRAPS: %d  |  STATO: %s",
                engine.getCoreHp(), engine.getDataScraps(), engine.getCurrentState()));

        // 2. Disegna i personaggi sulla griglia
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Optional<Entity> entityOpt = logicalGrid.getEntityAt(col, row);

                if (entityOpt.isPresent()) {
                    Entity e = entityOpt.get();
                    if (e instanceof Firewall) {
                        cellLabels[col][row].setText("F");
                        cellLabels[col][row].setTextFill(Color.CYAN);
                    } else if (e instanceof Virus) {
                        cellLabels[col][row].setText("V");
                        cellLabels[col][row].setTextFill(Color.LIMEGREEN);
                    }
                } else {
                    cellLabels[col][row].setText("");
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}