package application.controller;

import application.DAO.ProjetDAOImpl;
import application.Projet;
import application.vue.VueDashboard;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.util.Date;

public class ControleurDashboardAjouter implements EventHandler<ActionEvent> {

    private VueDashboard vue;
    private TextField champNom;
    private ProjetDAOImpl dao;

    public ControleurDashboardAjouter(VueDashboard vue, TextField champNom) {
        this.vue = vue;
        this.champNom = champNom;
        this.dao = new ProjetDAOImpl();
    }

    @Override
    public void handle(ActionEvent event) {
        String nom = champNom.getText();
        if (nom != null && !nom.trim().isEmpty()) {
            try {
                // 1. Création et Sauvegarde BDD
                Projet p = new Projet(nom, new Date());
                dao.save(p);

                // 2. Reset du champ
                champNom.clear();

                // 3. Demande à la vue de se rafraichir
                vue.chargerProjetsDepuisBDD();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la création du projet.");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Le nom du projet ne peut pas être vide.");
            alert.show();
        }
    }
}