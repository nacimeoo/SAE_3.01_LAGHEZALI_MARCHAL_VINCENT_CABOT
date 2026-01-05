package application.vue;

import application.*;
import application.controller.ControleurAjouterTache;
import application.controller.ControleurEditerTache;
import application.controller.ControleurRetourDashboard;
import application.controller.ControleurSupprimerTache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class VueListe extends BorderPane implements Observateur, VueProjet {
    private Projet projet;
    private ProjetService service;

    private HBox boardContainer;
    private TextField tfTask;

    private TacheAbstraite tacheSelectionnee = null;
    private HBox vueTacheSelectionnee = null;
    private Colonne colonneSelectionnee = null;
    private VBox vueColonneSelectionnee = null;

    public VueListe(Projet projet,  ProjetService projetService) {
        this.projet = projet;
        this.service = projetService;
        this.projet.enregistrerObservateur(this);
        initialiserComposant();
        rafraichirVue();
    }

    private void initialiserComposant(){
        this.setPadding(new Insets(15));

        // Header

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0,0,20,0));
        Button backButton = new Button("<- Dashboard");

        Label titreLabel = new Label(projet.getNom());
        titreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        header.getChildren().addAll(backButton,titreLabel);
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
        addTacheBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(1))));
        addTacheBox.setPadding(new Insets(10));

        Label lblAdd = new Label("Ajouter Tâche");
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

        ControleurSupprimerTache ctrlSupprTache = new ControleurSupprimerTache(projet, service, this);
        btnDelete.setOnAction(ctrlSupprTache);

        Button btnKanban = new Button("Vue Kanban");
        btnKanban.setMaxWidth(Double.MAX_VALUE);
        btnKanban.setStyle("-fx-background-color: #59a7ff; -fx-border-color: #000000;");

        sidebar.getChildren().addAll(addTacheBox, btnDelete,  btnKanban);
        this.setRight(sidebar);
    }

    private void rafraichirVue() {
        boardContainer.getChildren().clear();

        tacheSelectionnee = null;
        vueTacheSelectionnee = null;
        colonneSelectionnee = null;
        vueColonneSelectionnee = null;

        Map<LocalDate, List<TacheAbstraite>> tachesParDate = new TreeMap<>();

        for (TacheAbstraite t : projet.getAllTaches()) {
            if (t.getDate() != null) {
                tachesParDate.computeIfAbsent(t.getDate(), d -> new ArrayList<>()).add(t);
            }
        }

        for (LocalDate date : tachesParDate.keySet()) {
            VBox col = creerColonneDate(date, tachesParDate.get(date));
            boardContainer.getChildren().add(col);
        }

        System.out.println("Nb taches projet = " + projet.getAllTaches().size());
        for (TacheAbstraite t : projet.getAllTaches()) {
            System.out.println(t.getNom() + " -> " + t.getDate());
        }
    }


    private VBox creerColonneDate(LocalDate date, List<TacheAbstraite> taches) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)
        )));

        String titre = date.getDayOfWeek() + " " + date;
        Label lblDate = new Label(titre);
        lblDate.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        col.getChildren().add(lblDate);

        col.setOnMouseClicked(e -> {
            if (vueColonneSelectionnee != null) {
                vueColonneSelectionnee.setBorder(new Border(new BorderStroke(
                        Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)
                )));
            }
            vueColonneSelectionnee = col;
            col.setBorder(new Border(new BorderStroke(
                    Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)
            )));
            colonneSelectionnee = new Colonne(date.toString());
        });

        for (TacheAbstraite t : taches) {
            col.getChildren().add(creerCarteTache(t));
        }

        col.setOnDragOver(event -> {
            if (event.getGestureSource() != col && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        col.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String idStr = db.getString();
                try {
                    TacheAbstraite t = projet.getTacheById(idStr);
                    if (t != null) {

                        t.setDateDebut(date);

                        service.modifierTache(projet, t);

                        projet.notifierObservateurs();

                        success = true;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ID de tâche invalide pour le drag & drop : " + idStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        return col;
    }

    private HBox creerCarteTache(TacheAbstraite t) {
        HBox card = new HBox();
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: black; -fx-background-color: white;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(t.getNom());
        card.getChildren().add(lbl);

        card.setOnMouseClicked(e -> {
            e.consume();
            if (e.getClickCount() == 2) {
                new ControleurEditerTache(projet, service, t).handle(e);
            } else {
                if (vueTacheSelectionnee != null) {
                    vueTacheSelectionnee.setStyle("-fx-border-color: black; -fx-background-color: white;");
                }
                tacheSelectionnee = t;
                vueTacheSelectionnee = card;
                card.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: #e6f7ff;");
            }
        });

        card.setOnDragDetected(e -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString( String.valueOf(t.getId()));
            db.setContent(content);
            db.setDragView(card.snapshot(new SnapshotParameters(), null));
            e.consume();
        });

        return card;
    }

    public TacheAbstraite getTacheSelectionnee() { return tacheSelectionnee; }
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
}
