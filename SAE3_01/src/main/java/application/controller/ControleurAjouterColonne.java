package application.controller;

import application.Colonne;
import application.Projet;
import application.ProjetService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class ControleurAjouterColonne implements EventHandler<ActionEvent> {

    private Projet projet;
    private ProjetService service;

    public ControleurAjouterColonne(Projet projet, ProjetService service) {
        this.projet = projet;
        this.service = service;
    }

    @Override
    public void handle(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Nouvelle Colonne");
        dialog.setTitle("Création de colonne");
        dialog.setHeaderText("Entrez le nom de la nouvelle colonne :");
        dialog.setContentText("Nom :");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nom -> {
            if (!nom.trim().isEmpty()) {
                Colonne nouvelleCol = new Colonne(nom);
                try {
                    service.ajouterColonne(projet, nouvelleCol);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}