package application.controller;

import application.*;
import application.vue.VueProjet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class ControleurAjouterTache implements EventHandler<ActionEvent> {

    private final Projet projet;
    private final ProjetService service;
    private final VueProjet vue;
    private final TextField champ;

    public ControleurAjouterTache(Projet projet, ProjetService service,
                                  VueProjet vue, TextField champ) {
        this.projet = projet;
        this.service = service;
        this.vue = vue;
        this.champ = champ;
    }

    @Override
    public void handle(ActionEvent event) {

        String nom = champ.getText();
        if (nom == null || nom.isBlank()) return;

        TacheAbstraite tache = new TacheMere(nom);

        Colonne colonnePourBD;

        if (vue.estVueListe()) {
            colonnePourBD = projet.getColonnes().get(0);


            if (vue.getDateSelectionnee() != null) {
                tache.setDateDebut(vue.getDateSelectionnee());
            }

        } else {

            colonnePourBD = vue.getColonneSelectionnee();
        }

        if (colonnePourBD == null) return;

        try {
            service.ajouterTache(projet, colonnePourBD, tache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        champ.clear();
    }

}
