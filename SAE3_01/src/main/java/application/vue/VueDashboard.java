package application.vue;

import application.DAO.ProjetDAOImpl;
import application.MainApp;
import application.Projet;
import application.controller.ControleurDashboardAjouter;
import application.controller.ControleurDashboardOuvrir;
import application.controller.ControleurDashboardSupprimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class VueDashboard extends VBox {

    private MainApp mainApp;
    private ProjetDAOImpl projetDAO;
    private FlowPane projetsContainer;

    private Projet projetSelectionne = null;
    private Button btnSelectionne = null;

    private TextField nomInput;

    public VueDashboard(MainApp mainApp) {
        this.mainApp = mainApp;
        this.projetDAO = new ProjetDAOImpl();

        initialiserComposants();
        chargerProjetsDepuisBDD();
    }

    private void initialiserComposants() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);

        Label titrelabel = new Label("FRIDAY - DashBord");
        titrelabel.setFont(new Font("Arial", 30));

        VBox projetsSection = new VBox(10);
        projetsSection.setPadding(new Insets(15));
        projetsSection.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        Label projetTitre = new Label("Mes Projets");
        projetTitre.setFont(new Font("Arial", 18));

        projetsContainer = new FlowPane();
        projetsContainer.setHgap(20);
        projetsContainer.setVgap(20);
        projetsContainer.setPrefHeight(200);

        projetsSection.getChildren().addAll(projetTitre, projetsContainer);

        HBox bottomControls = new HBox(20);
        bottomControls.setAlignment(Pos.CENTER);

        nomInput = new TextField();
        nomInput.setPromptText("Nom du projet");

        Button btnAdd = new Button("Ajouter");
        ControleurDashboardAjouter ctrlAjout = new ControleurDashboardAjouter(this, nomInput);
        btnAdd.setOnAction(ctrlAjout);

        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
        ControleurDashboardSupprimer ctrlSuppr = new ControleurDashboardSupprimer(this);
        btnDelete.setOnAction(ctrlSuppr);

        Button btnOpen = new Button("Ouvrir");
        btnOpen.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: blue;");
        ControleurDashboardOuvrir ctrlOuvrir = new ControleurDashboardOuvrir(this, mainApp);
        btnOpen.setOnAction(ctrlOuvrir);

        bottomControls.getChildren().addAll(nomInput, btnAdd, btnOpen, btnDelete);
        this.getChildren().addAll(titrelabel, projetsSection, bottomControls);
    }

    public void chargerProjetsDepuisBDD() {
        projetsContainer.getChildren().clear();
        this.projetSelectionne = null;
        this.btnSelectionne = null;

        try {
            List<Projet> projets = projetDAO.getAllProjets();
            for (Projet p : projets) {
                Button btn = new Button(p.getNom());
                btn.setPrefSize(100, 60);
                btn.setOnAction(e -> selectionnerProjet(p, btn));
                projetsContainer.getChildren().add(btn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectionnerProjet(Projet p, Button btn) {
        if (btnSelectionne != null) {
            btnSelectionne.setStyle("");
        }

        this.projetSelectionne = p;
        this.btnSelectionne = btn;

        btn.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
    }

    public Projet getProjetSelectionne() {
        return projetSelectionne;
    }
}