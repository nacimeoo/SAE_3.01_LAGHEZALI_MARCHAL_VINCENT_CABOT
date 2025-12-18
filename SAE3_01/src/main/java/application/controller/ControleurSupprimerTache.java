package application.controller;

import application.Colonne;
import application.Projet;
import application.ProjetService;
import application.TacheAbstraite;
import application.vue.VueKanban;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

public class ControleurSupprimerTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private ProjetService service;
    private VueKanban vue;

    public ControleurSupprimerTache(Projet projet, ProjetService service, VueKanban vue) {
        this.projet = projet;
        this.service = service;
        this.vue = vue;
    }

    @Override
    public void handle(ActionEvent event) {

        TacheAbstraite tacheASupprimer = vue.getTacheSelectionnee();

        if (tacheASupprimer == null) {
            System.out.println("Aucune tâche sélectionnée !");
            return;
        }

        Colonne colonneContenante = null;
        for(Colonne c : projet.getColonnes()) {
            if(c.getTaches().contains(tacheASupprimer)) {
                colonneContenante = c;
                break;
            }
        }
        if (colonneContenante != null) {
            try {
                service.supprimerTache(projet,colonneContenante, tacheASupprimer);
                vue.resetSelection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
