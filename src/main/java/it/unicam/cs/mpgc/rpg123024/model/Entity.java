package it.unicam.cs.mpgc.rpg123024.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Firewall.class, name = "firewall"),
    @JsonSubTypes.Type(value = Virus.class, name = "virus")
})
public interface Entity {
    int getX();
    int getY();
    void setPosition(int x, int y);
    String getId();
    void takeDamage(int damage);
    boolean isActive();
    boolean canOccupy(int x, int y);
}