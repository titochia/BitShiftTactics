package it.unicam.cs.mpgc.rpg123024.model;

public interface Entity {
    int getX();
    int getY();
    void setPosition(int x, int y);
    String getId(); // Utile per riconoscere l'entità (es. "Virus_01")
}