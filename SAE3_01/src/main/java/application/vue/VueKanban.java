package application.vue;

import application.Colonne;
import application.DAO.ITacheDAO;
import application.DAO.TacheDAOImpl;
import application.controller.*;
import application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VueKanban extends BorderPane implements Observateur, VueProjet {

    private Projet projet;
    private ProjetService service;

    // Conteneurs graphiques
    private HBox boardContainer;
    private TextField tfTask;

    private TacheAbstraite tacheSelectionnee = null;
    private VBox vueTacheSelectionnee = null;
    private Colonne colonneSelectionnee = null;
    private VBox vueColonneSelectionnee = null;

    public VueKanban(Projet projet, ProjetService service) {
        this.projet = projet;
        this.service = service;

        this.projet.enregistrerObservateur(this);
        initialiserComposants();
        rafraichirVue();
    }

    private void initialiserComposants() {
        this.setPadding(new Insets(15));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Button backButton = new Button("<- Dashboard");


        Label titreLabel = new Label(projet.getNom());
        titreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        header.getChildren().addAll(backButton, titreLabel);
        this.setTop(header);

        boardContainer = new HBox(15);
        boardContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(boardContainer);
        scrollPane.setFitToHeight(true);
        this.setCenter(scrollPane);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(0, 0, 0, 15));
        sidebar.setPrefWidth(200);

        VBox addTacheBox = new VBox(10);
        addTacheBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        addTacheBox.setPadding(new Insets(10));

        VBox deleteBox = new VBox(10);
        Button btnListe = new Button("Vue Liste");
        btnListe.setMaxWidth(Double.MAX_VALUE);
        btnListe.setStyle("-fx-background-color: #59a7ff; -fx-border-color: #000000;");

        Label lblAdd = new Label("Ajouter Tache");
        tfTask = new TextField();
        tfTask.setPromptText("Nom de la tâche...");
        Button btnAdd = new Button("Ajouter");
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        ControleurAjouterTache ctrlAjout = new ControleurAjouterTache(projet, service, this, tfTask);
        btnAdd.setOnAction(ctrlAjout);

        addTacheBox.getChildren().addAll(lblAdd, tfTask, btnAdd);

        Button btnDelete = new Button("Supprimer Tâche");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");

        Button btnDeleteCol = new Button("Supprimer Colonne");
        btnDeleteCol.setMaxWidth(Double.MAX_VALUE);
        btnDeleteCol.setStyle("-fx-background-color: #ffe6cc; -fx-border-color: orange;");

        ControleurSupprimerTache ctrlSupprTache = new ControleurSupprimerTache(projet, service, this);
        btnDelete.setOnAction(ctrlSupprTache);

        ControleurSupprimerColonne ctrlSupprCol = new ControleurSupprimerColonne(projet, service, this);
        btnDeleteCol.setOnAction(ctrlSupprCol);

        sidebar.getChildren().addAll(addTacheBox, btnDelete, btnDeleteCol, btnListe);
        this.setRight(sidebar);
    }

    public TacheAbstraite getTacheSelectionnee() { return this.tacheSelectionnee; }
    public Colonne getColonneSelectionnee() { return this.colonneSelectionnee; }

    public void resetSelection() {
        this.tacheSelectionnee = null;
        if(this.vueTacheSelectionnee != null) {
            this.vueTacheSelectionnee.setStyle("-fx-border-color: black; -fx-background-color: white;");
        }
        this.vueTacheSelectionnee = null;

        this.colonneSelectionnee = null;
        if (this.vueColonneSelectionnee != null) {
            this.vueColonneSelectionnee.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        }
        this.vueColonneSelectionnee = null;
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Projet) {
            rafraichirVue();
        }
    }

    private void rafraichirVue() {
        boardContainer.getChildren().clear();

        this.vueTacheSelectionnee = null;
        this.vueColonneSelectionnee = null;
        this.tacheSelectionnee = null;
        this.colonneSelectionnee = null;

        for (int i = 0; i < projet.getColonnes().size(); i++) {
            Colonne c = projet.getColonnes().get(i);
            VBox colView = creeColVue(c, i);
            boardContainer.getChildren().add(colView);
        }

        Button btnAddCol = new Button("+ Ajouter Colonne");
        btnAddCol.setPrefSize(200, 50);
        btnAddCol.setStyle("-fx-border-color: gray; -fx-border-style: dashed; -fx-background-color: transparent; -fx-cursor: hand;");

        ControleurAjouterColonne ctrlAddCol = new ControleurAjouterColonne(projet, service);
        btnAddCol.setOnAction(ctrlAddCol);

        boardContainer.getChildren().add(btnAddCol);
    }

    private VBox creeColVue(Colonne c, int indexColonne) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setPrefWidth(200);
        col.setMinWidth(200);
        col.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        col.setOnMouseClicked(e -> {
            if (vueColonneSelectionnee != null) {
                vueColonneSelectionnee.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
            colonneSelectionnee = c;
            vueColonneSelectionnee = col;
            col.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        });

        col.setOnDragOver(event -> {
            if (event.getGestureSource() != col && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        col.setOnDragEntered(event -> {
            if (event.getGestureSource() != col && event.getDragboard().hasString()) {
                col.setStyle("-fx-background-color: #f0f0f0;");
            }
            event.consume();
        });

        col.setOnDragExited(event -> {
            col.setStyle("-fx-background-color: transparent;");
            event.consume();
        });

        ControleurDeplacerTache ctrlDrop = new ControleurDeplacerTache(projet, service, indexColonne);
        col.setOnDragDropped(ctrlDrop);

        col.getChildren().add(new Label(c.getNom()));

        for (TacheAbstraite t : c.getTaches()) {
            col.getChildren().add(createTaskCard(t, indexColonne));
        }
        return col;
    }

    private VBox createTaskCard(TacheAbstraite t, int indexColonneSource) {
        VBox cardContainer = new VBox(5);
        cardContainer.setPadding(new Insets(10));
        cardContainer.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label lblNom = new Label(t.getNom());
        lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        cardHeader.getChildren().add(lblNom);

        TacheAbstraite temp = t;
        while (temp instanceof TacheDecorateur) {
            if (temp instanceof Etiquette) {
                Etiquette e = (Etiquette) temp;
                Label labelEtiquette = new Label(e.getLibelle());
                labelEtiquette.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 2 5 2 5; -fx-background-radius: 3; -fx-font-size: 10;");
                cardHeader.getChildren().add(labelEtiquette);
            }
            temp = ((TacheDecorateur) temp).getTacheDecoree();
        }

        cardContainer.getChildren().add(cardHeader);

        cardContainer.setOnMouseClicked(e -> {
            e.consume();

            if (e.getClickCount() == 2) {
                new ControleurEditerTache(projet, service, t).handle(e);
            } else {
                if (vueTacheSelectionnee != null) {
                    ((Region)vueTacheSelectionnee).setStyle("-fx-border-color: black; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
                }
                tacheSelectionnee = t;
                vueTacheSelectionnee = cardContainer;
                cardContainer.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: #e6f7ff; -fx-background-radius: 5; -fx-border-radius: 5;");
            }
        });

        cardContainer.setOnDragDetected(event -> {
            Dragboard db = cardContainer.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(indexColonneSource + ":" + t.getId());
            db.setContent(content);
            event.consume();
        });

        TacheAbstraite core = t;
        while (core instanceof TacheDecorateur) {
            core = ((TacheDecorateur) core).getTacheDecoree();
        }

        if (core instanceof TacheMere) {
            TacheMere mere = (TacheMere) core;

            cardContainer.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    int idSource = Integer.parseInt(event.getDragboard().getString().split(":")[1]);
                    if (idSource != t.getId()) event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cardContainer.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    String[] parts = db.getString().split(":");
                    int colSourceIdx = Integer.parseInt(parts[0]);
                    int idFille = Integer.parseInt(parts[1]);
                    try {
                        TacheAbstraite fille = null;
                        Colonne currentCol = projet.getColonnes().get(colSourceIdx);
                        for(TacheAbstraite task : currentCol.getTaches()) if(task.getId() == idFille) fille = task;

                        if(fille != null && fille != t) {
                            service.ajouterDependance(projet, mere, fille, currentCol);
                            event.setDropCompleted(true);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
                event.consume();
            });

            VBox childrenBox = new VBox(5);
            childrenBox.setPadding(new Insets(5, 0, 0, 15)); // Indentation
            for (TacheAbstraite sous : mere.getSousTaches()) {
                childrenBox.getChildren().add(createTaskCard(sous, indexColonneSource));
            }
            cardContainer.getChildren().add(childrenBox);
        }

        return cardContainer;
    }
}