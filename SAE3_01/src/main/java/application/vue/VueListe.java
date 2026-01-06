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
import java.time.format.TextStyle;
import java.util.*;


public class VueListe extends BorderPane implements Observateur, VueProjet {
    private Projet projet;
    private ProjetService service;

    private VBox boardContainer;
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


        boardContainer = new VBox(15);
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
        btnKanban.setStyle("-fx-background-color: #a964d8; -fx-border-color: #000000;");

        Button btnGantt = new Button("Vue Gantt");
        btnGantt.setMaxWidth(Double.MAX_VALUE);
        btnGantt.setStyle("-fx-background-color: #a1d1f1; -fx-border-color: #000000;");

        sidebar.getChildren().addAll(addTacheBox, btnDelete,  btnKanban, btnGantt);
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
        String jour = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        jour = jour.substring(0,1).toUpperCase() + jour.substring(1);
        String titre = jour  + " " + date;
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
            col.getChildren().add(creerCarteTache(t, true));
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

    private VBox creerCarteTache(TacheAbstraite t, boolean afficherEntete) {

        VBox bloc = new VBox(1);
        bloc.setMaxWidth(Region.USE_PREF_SIZE);

        if (afficherEntete) {
            HBox entete = new HBox(6);
            entete.setPadding(new Insets(4, 6, 4, 6));
            entete.setAlignment(Pos.CENTER_LEFT);
            entete.setMaxWidth(Region.USE_PREF_SIZE);
            entete.setStyle("""
            -fx-background-color: #f0f0f0;
            -fx-border-color: black;
            -fx-border-width: 0 0 1 0;
            -fx-font-weight: bold;
            -fx-font-size: 12px;
        """);

            Label hNom = new Label("Nom");
            hNom.setPrefWidth(160);

            Label hPriorite = new Label("Priorité");
            hPriorite.setPrefWidth(70);

            Label hEtat = new Label("État");
            hEtat.setPrefWidth(90);

            Label hDuree = new Label("Durée");
            hDuree.setPrefWidth(70);

            Label hDescription = new Label("Description");
            hDescription.setPrefWidth(260);

            entete.getChildren().addAll(
                    hNom, hPriorite, hEtat, hDuree, hDescription
            );

            bloc.getChildren().add(entete);
        }

        HBox ligne = new HBox(6);
        ligne.setPadding(new Insets(4, 6, 4, 6));
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(Region.USE_PREF_SIZE);
        ligne.setStyle("""
        -fx-border-color: black;
        -fx-background-color: white;
        -fx-font-size: 12px;
    """);

        Label lblNom = new Label(t.getNom());
        lblNom.setPrefWidth(160);

        Label lblPriorite = new Label(
                t.getPriorite() >= 3 ? "Forte" :
                        t.getPriorite() == 1 ? "Faible" : "Moyenne"
        );
        lblPriorite.setPrefWidth(70);

        Label lblEtat = new Label(t.getEtat());
        lblEtat.setPrefWidth(90);

        Label lblDuree = new Label(t.getDureeEstimee() + " j");
        lblDuree.setPrefWidth(70);

        Label lblDescription = new Label(t.getDescription());
        lblDescription.setPrefWidth(260);

        ligne.getChildren().addAll(
                lblNom, lblPriorite, lblEtat, lblDuree, lblDescription
        );

        ligne.setOnMouseClicked(e -> {
            e.consume();
            if (e.getClickCount() == 2) {
                new ControleurEditerTache(projet, service, t).handle(e);
            } else {
                if (vueTacheSelectionnee != null) {
                    vueTacheSelectionnee.setStyle("""
                    -fx-border-color: black;
                    -fx-background-color: white;
                    -fx-font-size: 12px;
                """);
                }
                tacheSelectionnee = t;
                vueTacheSelectionnee = ligne;
                ligne.setStyle("""
                -fx-border-color: blue;
                -fx-border-width: 2;
                -fx-background-color: #e6f7ff;
                -fx-font-size: 12px;
            """);
            }
        });

        ligne.setOnDragDetected(e -> {
            Dragboard db = ligne.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(t.getId()));
            db.setContent(content);
            db.setDragView(ligne.snapshot(new SnapshotParameters(), null));
            e.consume();
        });

        bloc.getChildren().add(ligne);
        return bloc;
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
