package it.unicam.cs.mpgc.rpg123024.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("firewall")
public class Firewall extends AbstractCharacter {

    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;

    @JsonCreator
    public Firewall(@JsonProperty("id") String id, 
                    @JsonProperty("x") int x, 
                    @JsonProperty("y") int y,
                    @JsonProperty("hp") int hp) {
        super(id, x, y, hp);
        resetTurnFlags();
    }

    public Firewall(String id, int startX, int startY) {
        this(id, startX, startY, 3);
    }

    @Override
    public boolean canOccupy(int x, int y) {
        // REGOLA RED ZONE: I Firewall non possono entrare nella colonna 0
        return x != 0;
    }

    // Gestione della Durabilità (quando un virus ci sbatte contro)
    public void takeHit() {
        this.takeDamage(1);
    }

    public boolean isBroken() {
        return !this.isActive();
    }

    public int getDurability() {
        return this.getHp();
    }

    // Gestione delle Azioni del Turno
    public void resetTurnFlags() {
        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
    }

    public boolean canMove() { return !hasMovedThisTurn; }
    public void setMoved() { this.hasMovedThisTurn = true; }

    public boolean canAttack() { return !hasAttackedThisTurn; }
    public void setAttacked() { this.hasAttackedThisTurn = true; }
}