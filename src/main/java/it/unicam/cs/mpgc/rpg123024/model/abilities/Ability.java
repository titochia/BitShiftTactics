package it.unicam.cs.mpgc.rpg123024.model.abilities;

import it.unicam.cs.mpgc.rpg123024.model.Entity;

public interface Ability {
    String getName();
    int getRange(); //  Definisce la gittata dell'attacco
    void execute(Entity source, Entity target);
}