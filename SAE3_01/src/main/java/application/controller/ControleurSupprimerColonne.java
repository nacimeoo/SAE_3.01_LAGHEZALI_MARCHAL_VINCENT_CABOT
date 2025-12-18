package application.controller;

import application.Colonne;
import application.Projet;
import application.ProjetService;
import application.vue.VueKanban;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurSupprimerColonne implements EventHandler<ActionEvent> {

    private Projet projet;
    private ProjetService service;
    private VueKanban vue;

    public ControleurSupprimerColonne(Projet projet, ProjetService service, VueKanban vue) {
        this.projet = projet;
        this.service = service;
        this.vue = vue;
    }

    @Override
    public void handle(ActionEvent event) {
        Colonne colASupprimer = vue.getColonneSelectionnee();
        System.out.println("OK");
        if (colASupprimer != null) {
            try {
                service.supprimerColonne(projet, colASupprimer);
                vue.resetSelection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}