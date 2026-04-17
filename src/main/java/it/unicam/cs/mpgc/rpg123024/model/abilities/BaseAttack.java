package it.unicam.cs.mpgc.rpg123024.model.abilities;

import it.unicam.cs.mpgc.rpg123024.model.Entity;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;

/**
 * Rappresenta l'attacco standard di un difensore.
 * Colpisce in tutte le direzioni (incluso diagonale) entro un certo raggio.
 */
public class BaseAttack implements Ability {

    private final int damage = 40;
    private final int range = 1; // Gittata di 1 scacchi in ogni direzione

    @Override
    public String getName() {
        return "Attacco Base (Omnidirezionale)";
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public void execute(Entity source, Entity target) {
        // 1. Calcolo della Distanza di Chebyshev per il movimento diagonale libero
        int deltaX = Math.abs(source.getX() - target.getX());
        int deltaY = Math.abs(source.getY() - target.getY());
        int distance = Math.max(deltaX, deltaY);

        // 2. Controllo Range
        if (distance <= this.range) {

            // 3. Applichiamo il danno (solo se è un bersaglio distruttibile)
            // Usiamo il pattern matching di Java (se usi Java 16+) o il cast tradizionale
            if (target instanceof Virus) {
                Virus virus = (Virus) target;
                virus.takeDamage(this.damage);

                System.out.println(source.getId() + " ha colpito " + virus.getId() +
                        " per " + damage + " danni! (HP rimasti: " + virus.getHp() + ")");

            } else {
                System.out.println("Bersaglio non valido o indistruttibile.");
            }
        } else {
            System.out.println("Attacco fallito: bersaglio fuori portata. (Distanza: " + distance + ")");
        }
    }
}