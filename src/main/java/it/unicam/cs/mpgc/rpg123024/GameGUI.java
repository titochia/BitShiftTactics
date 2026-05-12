package it.unicam.cs.mpgc.rpg123024;

import it.unicam.cs.mpgc.rpg123024.engine.TurnManager;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;
import it.unicam.cs.mpgc.rpg123024.model.entities.TrojanVirus;
import it.unicam.cs.mpgc.rpg123024.persistence.PersistenceManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia Grafica di Bit Shift Tactics con rendering vettoriale avanzato.
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
        HBox hud = new HBox(15);
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(15));
        hud.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333; -fx-border-width: 0 0 2 0;");

        statsLabel = new Label();
        statsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        statsLabel.setTextFill(Color.WHITE);

        Button btnPassTurn = new Button("Passa Turno");
        btnPassTurn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnPassTurn.setOnAction(e -> handleEndTurn());

        Button btnSave = new Button("Salva");
        btnSave.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnSave.setOnAction(e -> handleSave());

        Button btnLoad = new Button("Carica");
        btnLoad.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnLoad.setOnAction(e -> handleLoad());

        Button btnSpawnEnemy = new Button("Test Virus");
        btnSpawnEnemy.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSpawnEnemy.setOnAction(e -> handleSpawnEnemy());

        hud.getChildren().addAll(statsLabel, btnPassTurn, btnSave, btnLoad, btnSpawnEnemy);
        root.setTop(hud);

        // --- 2. GRIGLIA DI GIOCO ---
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = new StackPane();
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
        Scene scene = new Scene(root, 850, 650);
        primaryStage.setTitle("Bit Shift Tactics");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        updateView();
    }

    private void handleCellClick(int col, int row) {
        if (engine.placeFirewall("FW_" + col + "_" + row, col, row)) {
            updateView();
        }
    }

    private void handleEndTurn() {
        engine.endPlayerTurn();
        updateView();
    }

    private void handleSave() {
        try {
            PersistenceManager.SaveData data = new PersistenceManager.SaveData(
                engine.getCoreHp(), engine.getDataScraps(), engine.getAllEntities()
            );
            new PersistenceManager().save(data);
            showAlert("Salvataggio", "Partita salvata correttamente!");
        } catch (IOException ex) {
            showAlert("Errore", "Errore nel salvataggio: " + ex.getMessage());
        }
    }

    private void handleLoad() {
        try {
            PersistenceManager.SaveData data = new PersistenceManager().load();
            logicalGrid = new Grid(COLS, ROWS);
            engine = new TurnManager(logicalGrid);
            engine.loadState(data.coreHp, data.dataScraps);
            
            for (Entity e : data.entities) {
                logicalGrid.placeEntity(e, e.getX(), e.getY());
                if (e instanceof Firewall) ((ArrayList)engine.getClass().getDeclaredField("activeFirewalls").get(engine)).add(e);
                if (e instanceof Virus) ((ArrayList)engine.getClass().getDeclaredField("activeViruses").get(engine)).add(e);
            }
            updateView();
            showAlert("Caricamento", "Partita ripristinata!");
        } catch (Exception ex) {
            showAlert("Errore", "Errore nel caricamento: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleSpawnEnemy() {
        int randomRow = (int) (Math.random() * ROWS);
        Virus v = (Math.random() < 0.3) 
            ? new TrojanVirus("TROJAN_" + System.currentTimeMillis(), 7, randomRow)
            : new Virus("V_TEST_" + System.currentTimeMillis(), 7, randomRow, 100);
        engine.spawnVirus(v);
        updateView();
    }

    private void updateView() {
        statsLabel.setText(String.format("CORE HP: %d  |  DATA SCRAPS: %d  |  STATO: %s",
                engine.getCoreHp(), engine.getDataScraps(), engine.getCurrentState()));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = getCellPaneAt(col, row);
                if (cellPane == null) continue;

                cellPane.getChildren().removeIf(node -> node instanceof Shape && node != cellPane.getChildren().get(0));

                Optional<Entity> entityOpt = logicalGrid.getEntityAt(col, row);
                if (entityOpt.isPresent()) {
                    Entity e = entityOpt.get();
                    if (e instanceof Firewall) cellPane.getChildren().add(createFirewallShape());
                    else if (e instanceof TrojanVirus) cellPane.getChildren().add(createTrojanShape());
                    else if (e instanceof Virus) cellPane.getChildren().add(createVirusShape());
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

    private Polygon createFirewallShape() {
        Polygon p = new Polygon(25,10, 55,10, 70,25, 70,55, 55,70, 25,70, 10,55, 10,25);
        p.setFill(Color.web("#1565C0")); p.setStroke(Color.CYAN); p.setStrokeWidth(3);
        return p;
    }

    private Polygon createVirusShape() {
        Polygon p = new Polygon(40,15, 48,32, 65,40, 48,48, 40,65, 32,48, 15,40, 32,32);
        p.setFill(Color.web("#8B0000")); p.setStroke(Color.web("#FF3333")); p.setStrokeWidth(3);
        return p;
    }

    private Polygon createTrojanShape() {
        Polygon p = createVirusShape();
        p.setFill(Color.GOLD); p.setStroke(Color.ORANGE);
        return p;
    }

    public static void main(String[] args) { launch(args); }
}
