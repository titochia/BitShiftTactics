# BitShift Tactics 🛡️👾

**BitShift Tactics** è un RPG tattico a turni sviluppato in Java, ambientato all'interno di un sistema informatico sotto attacco. Il progetto è stato realizzato per il corso di *Metodologie di Programmazione e Modellazione e Gestione della Conoscenza* (AA 2025/26).

## 🚀 Concept
Il giocatore assume il ruolo di un Amministratore di Sistema che deve difendere il **Core System** dall'invasione di malware. Posiziona i **Firewall**, muovili strategicamente sulla griglia e abbatti i **Virus** e i **Trojan** prima che compromettano l'integrità del sistema.

## 🛠️ Caratteristiche Tecniche
- **Architettura SOLID**: Il progetto segue rigorosamente i principi dell'ingegneria del software per garantire estensibilità e manutenibilità.
- **Grafica JavaFX**: Interfaccia reattiva con rendering vettoriale dei personaggi e feedback visivo (barre della salute dinamiche).
- **Persistenza JSON**: Sistema di salvataggio e caricamento dello stato della partita tramite la libreria Jackson.
- **Build System**: Gestito interamente con Gradle.

## 📦 Esecuzione
Il progetto richiede Java 21 o superiore.
1. Compilazione: `./gradlew build`
2. Esecuzione: `./gradlew run`

## 📖 Documentazione
Tutte le informazioni dettagliate su responsabilità delle classi, design pattern utilizzati e principi SOLID sono disponibili nella [Wiki di questo repository].

## 🤖 Dichiarazione uso AI
Si dichiara l'utilizzo di strumenti di AI (Gemini CLI) per il supporto al refactoring architettonico, la generazione di boilerplate per la persistenza e l'ottimizzazione del rendering grafico. Maggiori dettagli nella Wiki.
