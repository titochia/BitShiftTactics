package it.unicam.cs.mpgc.rpg123024.model.entities;

import it.unicam.cs.mpgc.rpg123024.model.Entity;

public abstract class AbstractCharacter implements Entity {
    private final String id;
    private int x;
    private int y;
    protected int hp;
    protected final int maxHp;

    public AbstractCharacter(String id, int startX, int startY, int hp) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.hp = hp;
        this.maxHp = hp;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String getId() { return id; }

    @Override
    public void takeDamage(int damage) {
        this.hp -= damage;
    }

    @Override
    public boolean isActive() {
        return this.hp > 0;
    }

    @Override
    public boolean canOccupy(int x, int y) {
        return true; // Comportamento di default: tutte le celle entro i limiti
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}