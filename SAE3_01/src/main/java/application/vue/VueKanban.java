package application.vue;

import application.controller.ControleurAjouterTache;
import application.controller.ControleurSupprimerTache;
import application.*;
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

public class VueKanban extends BorderPane implements Observateur {

    private Projet projet;

    // Conteneurs graphiques
    private HBox boardContainer;
    private TextField tfTask;

    private TacheAbstraite tacheSelectionnee = null;
    private HBox vueTacheSelectionnee = null;

    public VueKanban(Projet projet) {
        this.projet = projet;
        this.projet.enregistrerObservateur(this);
        initialiserComposants();
        rafraichirVue();
    }

    private void initialiserComposants() {
        this.setPadding(new Insets(15));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        Button backButton = new Button("<-");
        Label titleLabel = new Label(projet.getNom());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.getChildren().addAll(backButton, titleLabel);
        this.setTop(header);

        boardContainer = new HBox(15);
        boardContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(boardContainer);
        scrollPane.setFitToHeight(true);
        this.setCenter(scrollPane);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(0, 0, 0, 15));
        sidebar.setPrefWidth(200);

        VBox addTaskBox = new VBox(10);
        addTaskBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        addTaskBox.setPadding(new Insets(10));

        Label lblAdd = new Label("Ajouter Tache");
        tfTask = new TextField(); // Champ texte
        tfTask.setPromptText("Nom de la tâche...");
        Button btnAdd = new Button("Ajouter");
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        ControleurAjouterTache ctrlAjout = new ControleurAjouterTache(this.projet, this.tfTask);
        btnAdd.setOnAction(ctrlAjout);

        addTaskBox.getChildren().addAll(lblAdd, tfTask, btnAdd);

        Button btnDelete = new Button("Supprimer Sélection");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");

        ControleurSupprimerTache ctrlSuppr = new ControleurSupprimerTache(this.projet, this);
        btnDelete.setOnAction(ctrlSuppr);

        sidebar.getChildren().addAll(addTaskBox, btnDelete);
        this.setRight(sidebar);
    }

    public TacheAbstraite getTacheSelectionnee() {
        return this.tacheSelectionnee;
    }

    public void resetSelection() {
        this.tacheSelectionnee = null;
        this.vueTacheSelectionnee = null;
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Projet) {
            rafraichirVue();
        }
    }

    private void rafraichirVue() {
        boardContainer.getChildren().clear();
        for (Colonne c : projet.getColonnes()) {
            VBox colView = createColumnView(c);
            boardContainer.getChildren().add(colView);
        }
    }

    private VBox createColumnView(Colonne c) {
        VBox column = new VBox(10);
        column.setPadding(new Insets(10));
        column.setPrefWidth(200);
        column.setMinWidth(200);
        column.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        column.getChildren().add(new Label(c.getNom()));

        for (TacheAbstraite t : c.getTaches()) {
            column.getChildren().add(createTaskCard(t));
        }
        return column;
    }

    private HBox createTaskCard(TacheAbstraite t) {
        HBox card = new HBox();
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        if (t.equals(tacheSelectionnee)) {
            card.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: #e6f7ff;");
            vueTacheSelectionnee = card;
        } else {
            card.setStyle("-fx-border-color: black; -fx-background-color: white;");
        }

        Label lblName = new Label(t.getNom());
        card.getChildren().add(lblName);

        card.setOnMouseClicked(e -> {
            if (vueTacheSelectionnee != null) {
                vueTacheSelectionnee.setStyle("-fx-border-color: black; -fx-background-color: white;");
            }
            tacheSelectionnee = t;
            vueTacheSelectionnee = card;
            card.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: #e6f7ff;");
        });

        return card;
    }
}