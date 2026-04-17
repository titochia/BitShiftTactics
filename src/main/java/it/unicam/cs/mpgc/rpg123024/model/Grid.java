package it.unicam.cs.mpgc.rpg123024.model;

import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Grid {
    private final int width;
    private final int height;
    private final Map<Position, Entity> cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new HashMap<>();
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Controlla se il movimento è legale secondo le nuove regole
    public boolean canMoveTo(Entity entity, int targetX, int targetY) {
        if (!isWithinBounds(targetX, targetY)) {
            return false;
        }
        if (cells.containsKey(new Position(targetX, targetY))) {
            return false; // Cella già occupata
        }
        // REGOLA RED ZONE: I Firewall non possono entrare nella colonna 0
        if (entity instanceof Firewall && targetX == 0) {
            return false;
        }
        return true;
    }

    // Sposta fisicamente l'entità
    public boolean moveEntity(Entity entity, int targetX, int targetY) {
        if (canMoveTo(entity, targetX, targetY)) {
            // Rimuovi dalla vecchia posizione
            cells.remove(new Position(entity.getX(), entity.getY()));
            // Aggiorna coordinate entità
            entity.setPosition(targetX, targetY);
            // Inserisci nella nuova posizione
            cells.put(new Position(targetX, targetY), entity);
            return true;
        }
        return false;
    }

    public boolean placeEntity(Entity entity, int x, int y) {
        if (canMoveTo(entity, x, y)) {
            entity.setPosition(x, y);
            cells.put(new Position(x, y), entity);
            return true;
        }
        return false;
    }

    public void removeEntity(Entity entity) {
        cells.remove(new Position(entity.getX(), entity.getY()));
    }

    public Optional<Entity> getEntityAt(int x, int y) {
        return Optional.ofNullable(cells.get(new Position(x, y)));
    }
}