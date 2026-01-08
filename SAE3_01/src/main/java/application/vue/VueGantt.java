package application.vue;

import application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;

public class VueGantt extends BorderPane implements Observateur{

    private final Projet projet;
    private ProjetService service;

    private final int LARGEUR_JOUR = 75;
    private final int HAUTEUR_LIGNE = 50;


    public VueGantt(Projet projet, ProjetService service) {
        this.projet = projet;
        this.service = service;

        this.projet.enregistrerObservateur(this);
        initialiserComposants();
        rafraichirVue();
    }

    private void initialiserComposants(){
        this.setPadding(new Insets(15));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0,0,20,0));
        Button backButton = new Button("<- Dashboard");

        Label titreLabel = new Label(projet.getNom());
        titreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        header.getChildren().addAll(backButton,titreLabel);
        this.setTop(header);

        VBox boardContainer = new VBox(15);
        boardContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(boardContainer);
        scrollPane.setFitToHeight(true);
        this.setCenter(scrollPane);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(0, 0, 0, 15));
        sidebar.setPrefWidth(200);

        Button btnKanban = new Button("Vue Kanban");
        btnKanban.setMaxWidth(Double.MAX_VALUE);
        btnKanban.setStyle("-fx-background-color: #a964d8; -fx-border-color: #000000;");

        Button btnListe = new Button("Vue Liste");
        btnListe.setMaxWidth(Double.MAX_VALUE);
        btnListe.setStyle("-fx-background-color: #59a7ff; -fx-border-color: #000000;");

        sidebar.getChildren().addAll(btnKanban, btnListe);
        this.setRight(sidebar);
    }

    private void rafraichirVue() {
        Pane surfaceDessin = new Pane();
        surfaceDessin.setMinWidth(800);
        surfaceDessin.setMinHeight(600);

        LocalDate dateZero = (projet.getDateCreation() != null) ?
                new java.sql.Date(projet.getDateCreation().getTime()).toLocalDate() :
                LocalDate.now();

        for (int i = 0; i < 60; i++) {
            double x = i * LARGEUR_JOUR;

            javafx.scene.shape.Line ligne = new javafx.scene.shape.Line(x, 0, x, 1500);
            ligne.setStroke(Color.LIGHTGRAY);
            ligne.getStrokeDashArray().addAll(5d, 5d);

            Label dateLbl = new Label(dateZero.plusDays(i).toString());
            dateLbl.setLayoutX(x + 5);
            dateLbl.setLayoutY(0);
            dateLbl.setFont(Font.font("Arial", 10));

            surfaceDessin.getChildren().addAll(ligne, dateLbl);
        }

        int indexLigne = 1;

        for (Colonne colonne : projet.getColonnes()) {
            for(TacheAbstraite t : colonne.getTaches()) {
                indexLigne = dessinerTacheRecursive(t, dateZero, surfaceDessin, indexLigne, 0);
            }
        }
        ((ScrollPane) this.getCenter()).setContent(surfaceDessin);
    }

    private int dessinerTacheRecursive(TacheAbstraite t, LocalDate dateZero, Pane surfaceDessin, int indexLigne, int profondeur) {

        TacheAbstraite tacheReelle = t;

        if (t.getDateDebut() != null) {
            long joursEcoules = java.time.temporal.ChronoUnit.DAYS.between(dateZero, t.getDateDebut());
            if (joursEcoules < 0) joursEcoules = 0;

            double x = joursEcoules * LARGEUR_JOUR;
            double y = indexLigne * HAUTEUR_LIGNE;

            double duree = (t.getDureeEstimee() > 0) ? t.getDureeEstimee() : 1;
            double largeur = duree * LARGEUR_JOUR;

            javafx.scene.shape.Rectangle barre = new javafx.scene.shape.Rectangle(x, y, largeur, 40);
            barre.setArcWidth(10);
            barre.setArcHeight(10);

            Color couleurFond = genererCouleurParProfondeur(profondeur);
            barre.setFill(couleurFond);

            barre.setStroke(Color.DARKBLUE);
            barre.setStrokeWidth(0.5);

            Label nom = new Label(t.getNom());
            nom.setLayoutX(x + 5);
            nom.setLayoutY(y + 4);

            if (profondeur > 5) {
                nom.setTextFill(Color.BLACK);
            } else {
                nom.setTextFill(Color.WHITE);
            }

            surfaceDessin.getChildren().addAll(barre, nom);
        }

        int nouvelIndex = indexLigne + 1;

        while (tacheReelle instanceof TacheDecorateur) {
            tacheReelle = ((TacheDecorateur) tacheReelle).getTacheDecoree();
        }

        if (tacheReelle instanceof TacheMere) {
            TacheMere mere = (TacheMere) tacheReelle;
            for (TacheAbstraite enfant : mere.getSousTaches()) {
                nouvelIndex = dessinerTacheRecursive(enfant, dateZero, surfaceDessin, nouvelIndex, profondeur + 1);
            }
        }

        return nouvelIndex;
    }

    private Color genererCouleurParProfondeur(int profondeur) {
        Color base = Color.DARKBLUE;
        double facteur = Math.min(profondeur * 0.15, 0.8);

        return base.interpolate(Color.WHITE, facteur);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Projet) {
            rafraichirVue();
        }
    }

}

