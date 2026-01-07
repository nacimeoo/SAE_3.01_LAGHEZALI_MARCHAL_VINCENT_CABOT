package application.controller;

import application.Projet;
import application.ProjetService;
import application.TacheAbstraite;
import application.vue.VueDescriptionTache;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class ControleurEditerTache implements EventHandler<MouseEvent> {

    private Projet projet;
    private ProjetService service;
    private TacheAbstraite tache;

    public ControleurEditerTache(Projet projet, ProjetService service, TacheAbstraite tache) {
        this.projet = projet;
        this.service = service;
        this.tache = tache;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {

            VueDescriptionTache dialog = new VueDescriptionTache(tache, projet, service);


            Optional<TacheAbstraite> resultat = dialog.showAndWait();

            resultat.ifPresent(tacheModifiee -> {
                try {
                    service.modifierTache(projet, tacheModifiee);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}