package it.unicam.cs.mpgc.rpg123024.model.entities;

public class Virus extends AbstractCharacter {

    private int hp;

    public Virus(String id, int startX, int startY, int maxHp) {
        super(id, startX, startY);
        this.hp = maxHp;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) this.hp = 0;
    }

    public boolean isDestroyed() {
        return this.hp == 0;
    }

    public int getHp() {
        return this.hp;
    }
}