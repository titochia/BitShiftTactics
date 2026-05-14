package it.unicam.cs.mpgc.rpg123024.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicam.cs.mpgc.rpg123024.model.Entity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce il salvataggio e il caricamento dello stato del gioco.
 * Utilizza Jackson per la serializzazione JSON.
 */
public class PersistenceManager {

    private final ObjectMapper mapper;
    private static final String SAVE_FILE = "savegame.json";

    public PersistenceManager() {
        this.mapper = new ObjectMapper();
        // Ignora proprietà sconosciute per evitare crash tra versioni diverse
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.findAndRegisterModules();
    }

    /**
     * Salva i dati correnti del gioco.
     */
    public void save(SaveData data) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SAVE_FILE), data);
        System.out.println("Partita salvata con successo in " + SAVE_FILE);
    }

    /**
     * Carica i dati dall'ultimo salvataggio.
     */
    public SaveData load() throws IOException {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            throw new IOException("Nessun file di salvataggio trovato.");
        }
        return mapper.readValue(file, SaveData.class);
    }

    /**
     * Classe DTO (Data Transfer Object) per raggruppare i dati da salvare.
     */
    public static class SaveData {
        @JsonProperty("coreHp")
        public int coreHp;
        
        @JsonProperty("dataScraps")
        public int dataScraps;
        
        @JsonProperty("entities")
        public List<Entity> entities = new ArrayList<>();
        
        public SaveData() {}

        public SaveData(int coreHp, int dataScraps, List<Entity> entities) {
            this.coreHp = coreHp;
            this.dataScraps = dataScraps;
            this.entities = entities;
        }
    }
}
