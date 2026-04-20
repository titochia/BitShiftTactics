package it.unicam.cs.mpgc.rpg123024.engine;

import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;
import it.unicam.cs.mpgc.rpg123024.model.abilities.Ability;
import it.unicam.cs.mpgc.rpg123024.model.abilities.BaseAttack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TurnManager {

    private final Grid grid;
    private GameState currentState;
    private int coreHp;
    private int dataScraps;

    private final List<Firewall> activeFirewalls;
    private final List<Virus> activeViruses;

    public TurnManager(Grid grid) {
        this.grid = grid;
        this.currentState = GameState.PLAYER_TURN;
        this.coreHp = 100;
        this.dataScraps = 5; // Crediti iniziali
        this.activeFirewalls = new ArrayList<>();
        this.activeViruses = new ArrayList<>();
    }

    // --- METODI DI STATO ---
    public GameState getCurrentState() { return currentState; }
    public int getCoreHp() { return coreHp; }
    public int getDataScraps() { return dataScraps; }

    // --- AZIONI DEL GIOCATORE ---

    public boolean placeFirewall(String id, int x, int y) {
        if (currentState != GameState.PLAYER_TURN || dataScraps < 3) return false;

        Firewall fw = new Firewall(id, x, y);
        if (grid.placeEntity(fw, x, y)) {
            activeFirewalls.add(fw);
            dataScraps -= 3;
            return true;
        }
        return false;
    }

    public boolean moveFirewall(Firewall fw, int targetX, int targetY) {
        if (currentState != GameState.PLAYER_TURN || !fw.canMove()) return false;

        if (grid.moveEntity(fw, targetX, targetY)) {
            fw.setMoved(); // Registra che ha usato il movimento in questo turno
            return true;
        }
        return false;
    }

    public boolean attackWithFirewall(Firewall fw, Virus target) {
        // Controlla se è il tuo turno e se il firewall non ha già sparato
        if (currentState != GameState.PLAYER_TURN || !fw.canAttack()) {
            return false;
        }

        // Istanzia l'attacco base (il calcolo del range è delegato all'abilità)
        Ability attack = new BaseAttack();

        // Esegue l'attacco
        attack.execute(fw, target);

        // Registra che il Firewall ha usato la sua azione di attacco
        fw.setAttacked();

        return true;
    }

    public void sacrificeFirewall(Firewall fw) {
        if (currentState != GameState.PLAYER_TURN) return;

        grid.removeEntity(fw);
        activeFirewalls.remove(fw);
        this.coreHp += 20;
        if (this.coreHp > 100) this.coreHp = 100; // Limite massimo
        System.out.println("Firewall sacrificato! Core HP ripristinati a: " + this.coreHp);
    }

    // Aggiunge un virus alla partita in modo sicuro
    public boolean spawnVirus(Virus virus) {
        if (grid.placeEntity(virus, virus.getX(), virus.getY())) {
            activeViruses.add(virus);
            return true;
        }
        return false;
    }

    // --- TRANSIZIONE DI TURNO ---

    public void endPlayerTurn() {
        if (currentState != GameState.PLAYER_TURN) return;

        currentState = GameState.ENEMY_TURN;
        executeEnemyTurn();
    }

    private void executeEnemyTurn() {
        System.out.println("--- TURNO NEMICO ---");

        //Muovi tutti i virus
        for (Virus virus : activeViruses) {
            int nextX = virus.getX() - 1;
            int currentY = virus.getY();

            // Se arriva al Core System (x = 0)
            if (nextX == 0) {
                System.out.println("Allarme! Il virus " + virus.getId() + " ha colpito il Core!");
                this.coreHp -= 20;
                virus.takeDamage(999); // Il virus si distrugge dopo aver fatto danno
            } else {
                // Controlla cosa c'è davanti
                Optional<Entity> obstacle = grid.getEntityAt(nextX, currentY);
                if (obstacle.isPresent() && obstacle.get() instanceof Firewall) {
                    // C'è un Firewall, Sbattici contro
                    Firewall fw = (Firewall) obstacle.get();
                    fw.takeHit();
                    System.out.println("Virus bloccato da Firewall! Durabilità Firewall: " + fw.getDurability());
                } else {
                    // Via libera, avanza
                    grid.moveEntity(virus, nextX, currentY);
                }
            }
        }

        //Controlla morti e distruzioni (Pulizia Griglia)
        cleanupBoard();

        //Controlla Game Over
        if (this.coreHp <= 0) {
            currentState = GameState.GAME_OVER;
            System.out.println("GAME OVER - Il sistema è stato compromesso.");
            return;
        }

        // Ripristina le azioni dei Firewall per il nuovo turno
        for (Firewall fw : activeFirewalls) {
            fw.resetTurnFlags();
        }

        currentState = GameState.PLAYER_TURN;
        System.out.println("--- TURNO GIOCATORE ---");
    }

    // Rimuove i Firewall rotti e i Virus distrutti
    private void cleanupBoard() {
        Iterator<Firewall> fwIterator = activeFirewalls.iterator();
        while (fwIterator.hasNext()) {
            Firewall fw = fwIterator.next();
            if (fw.isBroken()) {
                grid.removeEntity(fw);
                fwIterator.remove();
                System.out.println("Un Firewall è andato in frantumi!");
            }
        }

        Iterator<Virus> virusIterator = activeViruses.iterator();
        while (virusIterator.hasNext()) {
            Virus v = virusIterator.next();
            if (v.isDestroyed()) {
                grid.removeEntity(v);
                virusIterator.remove();
                this.dataScraps++; // Guadagni risorse
                System.out.println("Virus distrutto! +1 Data Scrap.");
            }
        }
    }
}