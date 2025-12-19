package application.controller;

import application.*;
import application.vue.VueKanban;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class ControleurAjouterTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private ProjetService service;
    private VueKanban vue;
    private TextField champSaisie;

    public ControleurAjouterTache(Projet projet, ProjetService service, VueKanban vue, TextField champSaisie) {
        this.projet = projet;
        this.service = service;
        this.vue = vue;
        this.champSaisie = champSaisie;
    }

    @Override
    public void handle(ActionEvent event) {
        String nomTache = champSaisie.getText();
        if (nomTache != null && !nomTache.trim().isEmpty() && !projet.getColonnes().isEmpty()) {
            Colonne colSelectionnee = vue.getColonneSelectionnee();
            int indexColonne = 0;
            if (colSelectionnee != null) {
                indexColonne = projet.getColonnes().indexOf(colSelectionnee);
                if (indexColonne == -1) indexColonne = 0;
            }
            TacheAbstraite nouvelleTache = new TacheMere(nomTache);
            try {
                service.ajouterTache(projet,colSelectionnee, nouvelleTache);
                champSaisie.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            champSaisie.clear();
        }
    }
}