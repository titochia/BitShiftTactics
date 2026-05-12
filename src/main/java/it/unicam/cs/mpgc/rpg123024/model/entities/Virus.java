package it.unicam.cs.mpgc.rpg123024.model.entities;

public class Virus extends AbstractCharacter {

    public Virus(String id, int startX, int startY, int maxHp) {
        super(id, startX, startY, maxHp);
    }

    public boolean isDestroyed() {
        return !this.isActive();
    }
}