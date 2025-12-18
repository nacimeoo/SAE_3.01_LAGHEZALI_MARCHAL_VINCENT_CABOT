package application.controller;

import application.Colonne;
import application.Projet;
import application.ProjetService;
import application.TacheAbstraite;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;

public class ControleurDeplacerTache implements EventHandler<DragEvent> {

    private Projet projet;
    private ProjetService service;
    private int indexColonneDestination;

    public ControleurDeplacerTache(Projet projet, ProjetService service, int indexColonneDestination) {
        this.projet = projet;
        this.service = service;
        this.indexColonneDestination = indexColonneDestination;
    }

    @Override
    public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            String data = db.getString();
            String[] parts = data.split(":");

            if (parts.length == 2) {
                int indexColonneSource = Integer.parseInt(parts[0]);
                int idTache = Integer.parseInt(parts[1]);
                if (indexColonneSource != indexColonneDestination) {
                    Colonne colSource = projet.getColonnes().get(indexColonneSource);
                    Colonne colDest = projet.getColonnes().get(indexColonneDestination);
                    TacheAbstraite tache = null;
                    for(TacheAbstraite t : colSource.getTaches()) {
                        if(t.getId() == idTache) {
                            tache = t;
                            break;
                        }
                    }
                    if (tache != null) {
                        try {
                            service.deplacerTache(projet,colSource, colDest, tache);
                            success = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }
}