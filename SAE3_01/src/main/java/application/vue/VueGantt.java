package application.vue;

import application.*;
import application.controller.ControleurAjouterTache;
import application.controller.ControleurSupprimerTache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VueGantt extends BorderPane implements Observateur{

    private Projet projet;
    private ProjetService service;

    private VBox boardContainer;


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

        boardContainer = new VBox(15);
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
        boardContainer.getChildren().clear();

    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Projet) {
            rafraichirVue();
        }
    }

}

