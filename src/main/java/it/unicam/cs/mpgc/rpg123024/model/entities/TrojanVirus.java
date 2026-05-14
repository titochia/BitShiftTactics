package it.unicam.cs.mpgc.rpg123024.model.entities;

/**
 * Un nuovo tipo di virus che dimostra l'estensibilità del progetto.
 * Il Trojan ha più HP ma si muove più lentamente o ha comportamenti diversi.
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("trojan")
public class TrojanVirus extends Virus {

    @JsonCreator
    public TrojanVirus(@JsonProperty("id") String id, 
                       @JsonProperty("x") int x, 
                       @JsonProperty("y") int y,
                       @JsonProperty("hp") int hp) {
        super(id, x, y, hp);
    }

    public TrojanVirus(String id, int startX, int startY) {
        super(id, startX, startY, 80); // 2 colpi dell'attacco base (40 dmg)
    }

    @Override
    public String getId() {
        return "[TROJAN] " + super.getId();
    }
    
    // Possiamo sovrascrivere canOccupy se vogliamo regole diverse
}
