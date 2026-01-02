package application.controller;

import application.MainApp;
import application.Projet;
import application.vue.VueDashboard;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

public class ControleurDashboardOuvrir implements EventHandler<ActionEvent> {

    private VueDashboard vue;
    private MainApp mainApp;

    public ControleurDashboardOuvrir(VueDashboard vue, MainApp mainApp) {
        this.vue = vue;
        this.mainApp = mainApp;
    }

    @Override
    public void handle(ActionEvent event) {
        Projet projetSelectionne = vue.getProjetSelectionne();

        if (projetSelectionne != null) {
            mainApp.afficherKanban(projetSelectionne);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un projet à ouvrir.");
            alert.show();
        }
    }
}