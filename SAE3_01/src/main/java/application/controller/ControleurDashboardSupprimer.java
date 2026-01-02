package application.controller;

import application.DAO.ProjetDAOImpl;
import application.Projet;
import application.vue.VueDashboard;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

public class ControleurDashboardSupprimer implements EventHandler<ActionEvent> {

    private VueDashboard vue;
    private ProjetDAOImpl dao;

    public ControleurDashboardSupprimer(VueDashboard vue) {
        this.vue = vue;
        this.dao = new ProjetDAOImpl();
    }

    @Override
    public void handle(ActionEvent event) {
        Projet projetSelectionne = vue.getProjetSelectionne();
        if (projetSelectionne != null) {
            try {
                dao.delete(projetSelectionne.getId());
                vue.chargerProjetsDepuisBDD();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible de supprimer le projet.");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un projet à supprimer.");
            alert.show();
        }
    }
}