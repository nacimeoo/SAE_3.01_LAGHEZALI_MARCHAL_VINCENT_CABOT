package application.controller;

import application.Colonne;
import application.Projet;
import application.TacheAbstraite;
import application.vue.VueKanban;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurSupprimerTache implements EventHandler<ActionEvent> {

    private Projet projet;
    private VueKanban vue;

    public ControleurSupprimerTache(Projet projet, VueKanban vue) {
        this.projet = projet;
        this.vue = vue;
    }

    @Override
    public void handle(ActionEvent event) {

        TacheAbstraite tacheASupprimer = vue.getTacheSelectionnee();

        if (tacheASupprimer == null) {
            System.out.println("Aucune tâche sélectionnée !");
            return;
        }

        int indexColonne = -1;
        for (int i = 0; i < projet.getColonnes().size(); i++) {
            Colonne col = projet.getColonnes().get(i);
            if (col.getTaches().contains(tacheASupprimer)) {
                indexColonne = i;
                break;
            }
        }

        if (indexColonne != -1) {
            projet.supprimerTacheDeColonne(tacheASupprimer, indexColonne);
            vue.resetSelection();
        }
    }
}
