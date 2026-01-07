package application.vue;

import application.*;
import application.controller.ControleurAjouterTache;
import application.controller.ControleurEditerTache;
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
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class VueListe extends BorderPane implements Observateur, VueProjet {
    private Projet projet;
    private ProjetService service;
    private LocalDate dateSelectionnee = null;

    private VBox boardContainer;
    private TextField tfTask;

    private TacheAbstraite tacheSelectionnee = null;
    private HBox vueTacheSelectionnee = null;
    private Colonne colonneSelectionnee = null;
    private VBox vueColonneSelectionnee = null;

    public VueListe(Projet projet, ProjetService projetService) {
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

        header.getChildren().addAll(backButton, titreLabel);
        this.setTop(header);

        boardContainer = new VBox(15);
        boardContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(boardContainer);
        scrollPane.setFitToHeight(true);
        this.setCenter(scrollPane);

        // Sidebar
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

        sidebar.getChildren().addAll(addTacheBox, btnDelete, btnKanban, btnGantt);
        this.setRight(sidebar);
    }

    private void rafraichirVue() {
        boardContainer.getChildren().clear();
        resetSelection();

        Map<LocalDate, List<TacheAbstraite>> tachesParDate = new TreeMap<>();

        for (Colonne colonne : projet.getColonnes()) {
            for (TacheAbstraite t : colonne.getTaches()) {
                collecterTachesAvecDate(t, tachesParDate);
            }
        }

        for (LocalDate date : tachesParDate.keySet()) {
            VBox col = creerColonneDate(date, tachesParDate.get(date));
            boardContainer.getChildren().add(col);
        }
    }

    private void collecterTachesAvecDate(TacheAbstraite tache, Map<LocalDate, List<TacheAbstraite>> map) {
        if (tache == null) return;

        if (tache.getDate() != null) {
            map.computeIfAbsent(tache.getDate(), d -> new ArrayList<>()).add(tache);
        }

        TacheAbstraite core = tache;
        while (core instanceof TacheDecorateur) {
            core = ((TacheDecorateur) core).getTacheDecoree();
        }

        if (core instanceof TacheMere) {
            TacheMere mere = (TacheMere) core;
            for (TacheAbstraite sousTache : mere.getSousTaches()) {
                collecterTachesAvecDate(sousTache, map);
            }
        }
    }

    private VBox creerColonneDate(LocalDate date, List<TacheAbstraite> taches) {
        VBox col = new VBox(0); // VGap à 0 pour coller l'entête à la première ligne
        col.setPadding(new Insets(10));
        col.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)
        )));

        String jour = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        jour = jour.substring(0,1).toUpperCase() + jour.substring(1);
        String titre = jour  + " " + date;
        Label lblDate = new Label(titre);
        lblDate.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblDate.setPadding(new Insets(0, 0, 10, 0));

        col.getChildren().add(lblDate);

        col.setOnMouseClicked(e -> {
            if (vueColonneSelectionnee != null) {
                vueColonneSelectionnee.setBorder(new Border(new BorderStroke(
                        Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)
                )));
            }
            vueColonneSelectionnee = col;
            dateSelectionnee = date;
            col.setBorder(new Border(new BorderStroke(
                    Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)
            )));
            colonneSelectionnee = new Colonne(date.toString());
        });

        boolean isFirst = true;
        for (TacheAbstraite t : taches) {
            col.getChildren().add(creerCarteTache(t, isFirst));
            isFirst = false;
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
                try {
                    String idStr = db.getString();


                    TacheAbstraite t = projet.getTacheById(idStr);
                    if (t != null) {

                        t.setDateDebut(date);

                        service.ajusterDatesRecursifVersHaut(projet, t);

                        projet.notifierObservateurs();
                        success = true;
                    }
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
        VBox bloc = new VBox(0);
        bloc.setMaxWidth(Region.USE_PREF_SIZE);

        double widthNom = 200;
        double widthPrio = 70;
        double widthEtat = 90;
        double widthDuree = 70;
        double widthDesc = 260;

        if (afficherEntete) {
            HBox entete = new HBox(6);
            entete.setPadding(new Insets(4, 6, 4, 6));
            entete.setAlignment(Pos.CENTER_LEFT);
            entete.setMaxWidth(Region.USE_PREF_SIZE);
            entete.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black; -fx-border-width: 0 0 1 0; -fx-font-weight: bold; -fx-font-size: 12px;");

            Label hNom = new Label("Nom"); hNom.setPrefWidth(widthNom);
            Label hPriorite = new Label("Priorité"); hPriorite.setPrefWidth(widthPrio);
            Label hEtat = new Label("État"); hEtat.setPrefWidth(widthEtat);
            Label hDuree = new Label("Durée"); hDuree.setPrefWidth(widthDuree);
            Label hDescription = new Label("Description"); hDescription.setPrefWidth(widthDesc);

            entete.getChildren().addAll(hNom, hPriorite, hEtat, hDuree, hDescription);
            bloc.getChildren().add(entete);
        }

        HBox ligne = new HBox(6);
        ligne.setPadding(new Insets(4, 6, 4, 6));
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(Region.USE_PREF_SIZE);
        ligne.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-font-size: 12px;");
        if (!afficherEntete) {

            ligne.setStyle("-fx-border-color: black; -fx-border-width: 1 1 0 1; -fx-background-color: white; -fx-font-size: 12px;");
        }


        VBox h = new VBox();
        Label lblNom = new Label(t.getNom());
        lblNom.setPrefWidth(widthNom);

        FlowPane zoneEtiquettes = new FlowPane();
        zoneEtiquettes.setHgap(5);
        zoneEtiquettes.setVgap(3);
        zoneEtiquettes.setPrefWidth(widthNom);
        zoneEtiquettes.setMaxWidth(widthNom);
        zoneEtiquettes.setAlignment(Pos.CENTER_LEFT);

        TacheAbstraite current = t;
        while (current instanceof TacheDecorateur) {
            if (current instanceof Etiquette) {
                Etiquette et = (Etiquette) current;
                Label lblEtiquette = new Label(et.getLibelle());
                String hexColor = et.getCouleur().startsWith("0x") ? et.getCouleur().replace("0x", "#") : et.getCouleur();

                lblEtiquette.setStyle("-fx-background-color: " + hexColor + "; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3; -fx-font-size: 10px; -fx-font-weight: bold;");
                zoneEtiquettes.getChildren().add(lblEtiquette);
            }
            current = ((TacheDecorateur) current).getTacheDecoree();
        }
        h.getChildren().addAll(lblNom, zoneEtiquettes);

        Label lblPriorite = new Label(t.getPriorite() >= 3 ? "Forte" : t.getPriorite() == 1 ? "Faible" : "Moyenne");
        lblPriorite.setPrefWidth(widthPrio);

        Label lblEtat = new Label(t.getEtat());
        lblEtat.setPrefWidth(widthEtat);

        Label lblDuree = new Label(t.getDureeEstimee() + " j");
        lblDuree.setPrefWidth(widthDuree);

        Label lblDescription = new Label(t.getDescription());
        lblDescription.setPrefWidth(widthDesc);

        ligne.getChildren().addAll(h, lblPriorite, lblEtat, lblDuree, lblDescription);

        ligne.setOnMouseClicked(e -> {
            e.consume();
            if (e.getClickCount() == 2) {
                new ControleurEditerTache(projet, service, t).handle(e);
            } else {
                if (vueTacheSelectionnee != null) {
                    vueTacheSelectionnee.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-font-size: 12px;");
                }
                tacheSelectionnee = t;
                vueTacheSelectionnee = ligne;
                ligne.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: #e6f7ff; -fx-font-size: 12px;");
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

    @Override
    public boolean estVueListe() {
        return true;
    }

    @Override
    public LocalDate getDateSelectionnee() {
        return dateSelectionnee;
    }

    private void selectionnerDate(LocalDate date) {
        this.dateSelectionnee = date;
    }

}