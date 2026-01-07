package application.controller;

import application.Colonne;
import application.Projet;
import application.ProjetService;
import application.TacheAbstraite;
import application.vue.VueProjet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurSupprimerTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private ProjetService service;
    private VueProjet vue;

    public ControleurSupprimerTache(Projet projet, ProjetService service, VueProjet vue) {
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

        Colonne colonneContenante;


        if (vue.estVueListe()) {
            colonneContenante = projet.getColonnes().get(0);
        } else {

            colonneContenante = vue.getColonneSelectionnee();
        }

        if (colonneContenante != null) {
            try {
                service.supprimerTache(projet, colonneContenante, tacheASupprimer);
                vue.resetSelection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : Impossible de déterminer la colonne de la tâche.");
        }
    }
}
