package application.controller;

import application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import java.util.Random;

public class ControleurAjouterTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private TextField champSaisie;

    public ControleurAjouterTache(Projet projet, TextField champSaisie) {
        this.projet = projet;
        this.champSaisie = champSaisie;
    }

    @Override
    public void handle(ActionEvent event) {
        String nomTache = champSaisie.getText();

        if (nomTache != null && !nomTache.trim().isEmpty() && !projet.getColonnes().isEmpty()) {

            TacheAbstraite nouvelleTache = new TacheMere( nomTache);

            projet.ajouterTacheDansColonne(nouvelleTache, 0);

            champSaisie.clear();
        }
    }
}