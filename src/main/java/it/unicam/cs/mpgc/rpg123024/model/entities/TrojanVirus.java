package it.unicam.cs.mpgc.rpg123024.model.entities;

/**
 * Un nuovo tipo di virus che dimostra l'estensibilità del progetto.
 * Il Trojan ha più HP ma si muove più lentamente o ha comportamenti diversi.
 */
public class TrojanVirus extends Virus {

    public TrojanVirus(String id, int startX, int startY) {
        super(id, startX, startY, 120); // Più resistente di un virus normale
    }

    @Override
    public String getId() {
        return "[TROJAN] " + super.getId();
    }
    
    // Possiamo sovrascrivere canOccupy se vogliamo regole diverse
}
