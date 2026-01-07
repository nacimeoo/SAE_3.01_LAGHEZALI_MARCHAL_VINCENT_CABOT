package application.vue;

import application.Colonne;
import application.Etiquette;
import application.Projet;
import application.ProjetService;
import application.Sujet;
import application.TacheAbstraite;
import application.TacheDecorateur;
import application.TacheMere;
import application.Observateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class VueArchive extends BorderPane implements Observateur, VueProjet {

    private Projet projet;
    private ProjetService service;
    private VBox containerTaches;

    public VueArchive(Projet projet, ProjetService service) {
        this.projet = projet;
        this.service = service;
        this.projet.enregistrerObservateur(this);
        initialiserComposants();
        rafraichirVue();
    }

    private void initialiserComposants() {
        this.setPadding(new Insets(20));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Button btnRetour = new Button("<- Retour Kanban");

        Label titreLabel = new Label("Archives : " + projet.getNom());
        titreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        header.getChildren().addAll(btnRetour, titreLabel);
        this.setTop(header);

        containerTaches = new VBox(10);
        containerTaches.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(containerTaches);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        this.setCenter(scrollPane);
    }

    @Override
    public void actualiser(Sujet s) {
        rafraichirVue();
    }

    private void rafraichirVue() {
        containerTaches.getChildren().clear();
        List<TacheAbstraite> archives = collecterTachesArchivees();

        if (archives.isEmpty()) {
            Label lblVide = new Label("La corbeille d'archives est vide.");
            lblVide.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            containerTaches.getChildren().add(lblVide);
        } else {
            for (TacheAbstraite t : archives) {
                containerTaches.getChildren().add(creerCarteArchive(t));
            }
        }
    }

    private List<TacheAbstraite> collecterTachesArchivees() {
        List<TacheAbstraite> resultats = new ArrayList<>();
        for (Colonne c : projet.getColonnes()) {
            for (TacheAbstraite t : c.getTaches()) {
                chercherArchivesRecursif(t, resultats);
            }
        }
        return resultats;
    }

    private void chercherArchivesRecursif(TacheAbstraite t, List<TacheAbstraite> resultats) {
        if ("Archivée".equals(t.getEtat())) {
            resultats.add(t);
        }
        TacheAbstraite core = t;
        while (core instanceof TacheDecorateur) core = ((TacheDecorateur) core).getTacheDecoree();
        if (core instanceof TacheMere) {
            for (TacheAbstraite sous : ((TacheMere) core).getSousTaches()) {
                chercherArchivesRecursif(sous, resultats);
            }
        }
    }

    private HBox creerCarteArchive(TacheAbstraite t) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        VBox infoBox = new VBox(5);
        Label lblNom = new Label(t.getNom());
        lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblDesc = new Label(t.getDescription());
        infoBox.getChildren().addAll(lblNom, lblDesc);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button btnRestaurer = new Button("Restaurer");
        btnRestaurer.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        btnRestaurer.setOnAction(e -> {
            try {
                service.changerEtat(projet, t, "A faire");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(infoBox, btnRestaurer);
        return card;
    }

    @Override
    public TacheAbstraite getTacheSelectionnee() {
        return null;
    }

    @Override
    public void resetSelection() {

    }

    @Override
    public Colonne getColonneSelectionnee() {
        return null;
    }
}