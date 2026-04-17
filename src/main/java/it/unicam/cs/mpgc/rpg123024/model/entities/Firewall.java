package it.unicam.cs.mpgc.rpg123024.model.entities;

public class Firewall extends AbstractCharacter {

    private int durability;

    // Per gestire il turno dinamico
    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;

    public Firewall(String id, int startX, int startY) {
        super(id, startX, startY);
        this.durability = 3; // 3 cariche base
        resetTurnFlags();
    }

    // Gestione della Durabilità (quando un virus ci sbatte contro)
    public void takeHit() {
        this.durability--;
    }

    public boolean isBroken() {
        return this.durability <= 0;
    }

    public int getDurability() {
        return this.durability;
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