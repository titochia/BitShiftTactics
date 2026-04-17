package it.unicam.cs.mpgc.rpg123024.model.entities;

import it.unicam.cs.mpgc.rpg123024.model.Entity;

public abstract class AbstractCharacter implements Entity {
    private final String id;
    private int x;
    private int y;

    public AbstractCharacter(String id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
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
}