package application.controller;

import application.*;
import application.vue.VueKanban;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class ControleurAjouterTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private VueKanban vue;
    private TextField champSaisie;

    public ControleurAjouterTache(Projet projet, VueKanban vue, TextField champSaisie) {
        this.projet = projet;
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
            projet.ajouterTacheDansColonne(nouvelleTache, indexColonne);

//            projet.ajouterTacheDansColonne(nouvelleTache, 0);

            champSaisie.clear();
        }
    }
}