package it.unicam.cs.mpgc.rpg123024;

import it.unicam.cs.mpgc.rpg123024.engine.TurnManager;
import it.unicam.cs.mpgc.rpg123024.model.Grid;
import it.unicam.cs.mpgc.rpg123024.model.entities.Firewall;
import it.unicam.cs.mpgc.rpg123024.model.entities.Virus;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== AVVIO BIT SHIFT TACTICS (CONSOLE TEST) ===");

        // 1. Inizializziamo il mondo
        Grid grid = new Grid(8, 5); // Griglia 8x5
        TurnManager engine = new TurnManager(grid);

        System.out.println("Risorse iniziali: " + engine.getDataScraps());
        System.out.println("HP Core System: " + engine.getCoreHp());

        // 2. Il Giocatore piazza le sue difese (ha 5 risorse, un firewall costa 3)
        System.out.println("\n[TURNO GIOCATORE] Piazzamento difese...");
        boolean piazzato = engine.placeFirewall("FW_Alpha", 2, 2);
        System.out.println("Firewall piazzato con successo? " + piazzato);
        System.out.println("Risorse rimanenti: " + engine.getDataScraps());

        // 3. Recuperiamo il Firewall appena piazzato dalla griglia per potergli dare ordini
        Firewall mioFirewall = (Firewall) grid.getEntityAt(2, 2).orElseThrow();

        // 4. Creiamo un Virus nemico (lo mettiamo in X=4, Y=2, quindi a distanza 2 dal Firewall)
        // Nota: Nel gioco reale sarà l'engine a crearli da solo tramite il WaveManager
        Virus virusNemico = new Virus("Trojan_01", 4, 2, 100);
        grid.placeEntity(virusNemico, 4, 2);

        // Per testare, aggiungiamo forzatamente il virus alla lista interna dell'engine
        // (questo serve solo per questo test manuale)
        try {
            java.lang.reflect.Field field = TurnManager.class.getDeclaredField("activeViruses");
            field.setAccessible(true);
            ((java.util.List<Virus>) field.get(engine)).add(virusNemico);
        } catch (Exception e) { e.printStackTrace(); }

        // 5. Test dell'attacco! (Il virus è in X=4, Firewall in X=2. Distanza = 2. Range = 1. L'attacco FALLIRÀ!)
        System.out.println("\n[TURNO GIOCATORE] Tentativo di attacco...");
        engine.attackWithFirewall(mioFirewall, virusNemico);

        // 6. Fine turno giocatore, i virus avanzano
        System.out.println("\nPassaggio del turno al nemico...");
        engine.endPlayerTurn();

        // 7. Il virus si è mosso di 1 verso sinistra. Ora è in X=3.
        // Distanza dal Firewall (X=2) è diventata 1. Ora è in range!
        System.out.println("\n[TURNO GIOCATORE] Nuovo turno. Riprovo ad attaccare...");
        engine.attackWithFirewall(mioFirewall, virusNemico);

        System.out.println("\n=== FINE TEST ===");
    }
}