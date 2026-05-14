package it.unicam.cs.mpgc.rpg123024;

import it.unicam.cs.mpgc.rpg123024.engine.TurnManager;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;
import it.unicam.cs.mpgc.rpg123024.model.entities.TrojanVirus;
import it.unicam.cs.mpgc.rpg123024.model.entities.AbstractCharacter;
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
import javafx.scene.layout.VBox;
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
 * Interfaccia Grafica di Bit Shift Tactics migliorata.
 */
public class GameGUI extends Application {

    private static final int COLS = 8;
    private static final int ROWS = 5;
    private static final int CELL_SIZE = 80;

    private TurnManager engine;
    private Grid logicalGrid;
    private Label statsLabel;
    private GridPane gridPane;
    private Entity selectedEntity = null;

    @Override
    public void start(Stage primaryStage) {
        logicalGrid = new Grid(COLS, ROWS);
        engine = new TurnManager(logicalGrid);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: BLACK;");

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

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = new StackPane();
                cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
                cellPane.setMaxSize(CELL_SIZE, CELL_SIZE);

                Rectangle bg = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
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
        Optional<Entity> clickedEntity = logicalGrid.getEntityAt(col, row);

        if (clickedEntity.isPresent()) {
            Entity e = clickedEntity.get();
            if (e instanceof Firewall) {
                selectedEntity = e;
                System.out.println("Selezionato Firewall: " + e.getId());
            } else if (e instanceof Virus && selectedEntity instanceof Firewall) {
                // ATTACCO
                if (engine.attackWithFirewall((Firewall) selectedEntity, (Virus) e)) {
                    selectedEntity = null;
                } else {
                    showAlert("Azione non valida", "Attacco fallito! (Range o azione già usata)");
                }
            }
        } else {
            // Cella vuota
            if (selectedEntity instanceof Firewall) {
                // MOVIMENTO
                if (engine.moveFirewall((Firewall) selectedEntity, col, row)) {
                    System.out.println("Movimento riuscito!");
                    selectedEntity = null; // Deseleziona dopo il movimento
                } else {
                    // Se il movimento fallisce (es. già mosso o troppo lontano), prova a piazzare un NUOVO Firewall
                    if (engine.placeFirewall("FW_" + System.currentTimeMillis(), col, row)) {
                        selectedEntity = null;
                    }
                }
            } else {
                // Nessuna selezione: prova a piazzare
                if (engine.placeFirewall("FW_" + System.currentTimeMillis(), col, row)) {
                    selectedEntity = null;
                }
            }
        }
        updateView();
    }

    private void handleEndTurn() {
        engine.endPlayerTurn();
        selectedEntity = null;
        updateView();
    }

    private void handleSave() {
        try {
            List<Entity> entities = new ArrayList<>();
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    logicalGrid.getEntityAt(c, r).ifPresent(entities::add);
                }
            }
            PersistenceManager.SaveData data = new PersistenceManager.SaveData(
                engine.getCoreHp(), engine.getDataScraps(), entities
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
                if (logicalGrid.placeEntity(e, e.getX(), e.getY())) {
                    engine.addLoadedEntity(e);
                }
            }
            selectedEntity = null;
            updateView();
            showAlert("Caricamento", "Partita caricata!");
        } catch (Exception ex) {
            ex.printStackTrace(); // Stampa l'errore completo nella console di IntelliJ
            showAlert("Errore di Caricamento", 
                "Dettagli: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    private void updateView() {
        statsLabel.setText(String.format("CORE HP: %d  |  DATA SCRAPS: %d  |  STATO: %s",
                engine.getCoreHp(), engine.getDataScraps(), engine.getCurrentState()));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cellPane = getCellPaneAt(col, row);
                if (cellPane == null) continue;

                // RIMUOVI TUTTO tranne lo sfondo (indice 0)
                while (cellPane.getChildren().size() > 1) {
                    cellPane.getChildren().remove(1);
                }

                Rectangle bg = (Rectangle) cellPane.getChildren().get(0);
                bg.setStroke(Color.BLACK);
                bg.setStrokeWidth(1);

                Optional<Entity> entityOpt = logicalGrid.getEntityAt(col, row);
                if (entityOpt.isPresent()) {
                    Entity e = entityOpt.get();
                    if (e == selectedEntity) {
                        bg.setStroke(Color.YELLOW);
                        bg.setStrokeWidth(3);
                    }

                    if (e instanceof Firewall) cellPane.getChildren().add(createFirewallShape());
                    else if (e instanceof TrojanVirus) cellPane.getChildren().add(createTrojanShape());
                    else if (e instanceof Virus) cellPane.getChildren().add(createVirusShape());

                    cellPane.getChildren().add(createHealthBarUI(e));
                }
            }
        }
    }

    private VBox createHealthBarUI(Entity e) {
        double maxHp = e.getMaxHp();
        double currentHp = (e instanceof AbstractCharacter) ? ((AbstractCharacter) e).getHp() : 0;
        
        Rectangle background = new Rectangle(60, 6, Color.web("#444444"));
        background.setArcWidth(4); background.setArcHeight(4);
        
        double ratio = Math.max(0, currentHp) / maxHp;
        Rectangle foreground = new Rectangle(60 * ratio, 6, Color.web(ratio > 0.5 ? "#2ecc71" : "#e74c3c"));
        foreground.setArcWidth(4); foreground.setArcHeight(4);
        
        StackPane bar = new StackPane(background, foreground);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(60);
        
        VBox container = new VBox(bar);
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setPadding(new Insets(0, 0, 8, 0));
        container.setMouseTransparent(true); // Evita che la barra blocchi i click
        return container;
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

    private void handleSpawnEnemy() {
        int randomRow = (int) (Math.random() * ROWS);
        Virus v = (Math.random() < 0.3) 
            ? new TrojanVirus("TROJAN_" + System.currentTimeMillis(), 7, randomRow)
            : new Virus("VIRUS_" + System.currentTimeMillis(), 7, randomRow, 40);
        engine.spawnVirus(v);
        updateView();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
