package it.unicam.cs.mpgc.rpg123024.model.entities;

public class Firewall extends AbstractCharacter {

    // Per gestire il turno dinamico
    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;

    public Firewall(String id, int startX, int startY) {
        super(id, startX, startY, 3); // 3 HP = Durabilità
        resetTurnFlags();
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