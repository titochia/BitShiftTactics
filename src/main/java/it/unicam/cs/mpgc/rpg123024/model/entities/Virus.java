package it.unicam.cs.mpgc.rpg123024.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("virus")
public class Virus extends AbstractCharacter {

    @JsonCreator
    public Virus(@JsonProperty("id") String id, 
                 @JsonProperty("x") int x, 
                 @JsonProperty("y") int y, 
                 @JsonProperty("hp") int hp) {
        super(id, x, y, hp);
    }

    public boolean isDestroyed() {
        return !this.isActive();
    }
}