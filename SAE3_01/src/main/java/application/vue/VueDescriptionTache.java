package application.vue;

import application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class VueDescriptionTache extends Dialog<TacheAbstraite> {

    private TacheAbstraite tacheEnCoursEdition;
    private Projet projet;
    private ProjetService projetService;

    public VueDescriptionTache(TacheAbstraite tache,  Projet projet, ProjetService projetService) {
        this.tacheEnCoursEdition = tache;
        this.projet = projet;
        this.projetService = projetService;

        this.setTitle("Détails de la tâche");
        this.setHeaderText("Modifier la tâche : " + tache.getNom());

        ButtonType loginButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfTitre = new TextField(tache.getNom());
        TextArea taDesc = new TextArea(tache.getDescription());
        taDesc.setPromptText("Description...");
        taDesc.setPrefRowCount(3);

        ComboBox<String> cbEtat = new ComboBox<>();
        cbEtat.getItems().addAll("A faire", "En cours", "Terminer", "En attente");
        cbEtat.setValue(tache.getEtat() != null ? tache.getEtat() : "A faire");

        ComboBox<String> cbPriorite = new ComboBox<>();
        cbPriorite.getItems().addAll("Faible", "Moyenne", "Haute");

        int currentPrio = tache.getPriorite();
        if (currentPrio >= 3) {
            cbPriorite.setValue("Haute");
        } else if (currentPrio == 1) {
            cbPriorite.setValue("Faible");
        } else {
            cbPriorite.setValue("Moyenne");
        }

        DatePicker dpDate = new DatePicker();
        if (tache.getDateDebut() != null) {
            dpDate.setValue(tache.getDateDebut());
        }

        TextField tfDuree = new TextField();
        tfDuree.setPromptText("En minutes (ex: 60)");
        tfDuree.setText(String.valueOf(tache.getDureeEstimee()));

        FlowPane zoneEtiquettes = new FlowPane();
        zoneEtiquettes.setHgap(5);
        updateAffichageEtiquettes(zoneEtiquettes);

        TextField tfEtiquetteNom = new TextField();
        tfEtiquetteNom.setPromptText("Nom étiquette");
        ColorPicker colorPicker = new ColorPicker(Color.RED);
        colorPicker.setPrefWidth(100);
        Button btnAddEtiquette = new Button("+");

        HBox boxAjoutEtiquette = new HBox(5, tfEtiquetteNom, colorPicker, btnAddEtiquette);

        btnAddEtiquette.setOnAction(e -> {
            String nomEtiquette = tfEtiquetteNom.getText();
            if (!nomEtiquette.isEmpty()) {
                String webColor = colorPicker.getValue().toString().replace("0x", "#");
                this.tacheEnCoursEdition = new Etiquette(this.tacheEnCoursEdition, nomEtiquette, webColor);

                updateAffichageEtiquettes(zoneEtiquettes);
                tfEtiquetteNom.clear();
            }
        });


        grid.add(new Label("Titre:"), 0, 0);
        grid.add(tfTitre, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(taDesc, 1, 1);
        grid.add(new Label("Etat:"), 0, 2);
        grid.add(cbEtat, 1, 2);
        grid.add(new Label("Priorité:"), 0, 3);
        grid.add(cbPriorite, 1, 3);
        grid.add(new Label("Date Début:"), 0, 4);
        grid.add(dpDate, 1, 4);
        grid.add(new Label("Durée (en Jours) :"), 0, 5);
        grid.add(tfDuree, 1, 5);
        grid.add(new Label("Etiquettes:"), 0, 6);
        grid.add(zoneEtiquettes, 1, 6);
        grid.add(new Label("Nouvelle:"), 0, 7);
        grid.add(boxAjoutEtiquette, 1, 7);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                tacheEnCoursEdition.setNom(tfTitre.getText());
                tacheEnCoursEdition.setDescription(taDesc.getText());

                String nouvelEtat = cbEtat.getValue();
                String etatActuel = tacheEnCoursEdition.getEtat();

                if (!etatActuel.equals(nouvelEtat)) { // seulement si l'état a été modifié
                    if (projetService.verifierEtatTacheMere(tacheEnCoursEdition)) {
                        tacheEnCoursEdition.setEtat(nouvelEtat);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING,
                                "Impossible de modifier l'état : toutes les sous-tâches ne sont pas terminées.");
                        alert.setHeaderText("État invalide");
                        alert.showAndWait();
                        
                    }
                }

                tacheEnCoursEdition.setDescription(taDesc.getText());

                String prioTexte = cbPriorite.getValue();
                int prioInt = 2;
                if ("Faible".equals(prioTexte)) prioInt = 1;
                else if ("Haute".equals(prioTexte)) prioInt = 3;

                tacheEnCoursEdition.setPriorite(prioInt);

                if (projetService.verifierReglesDates(projet, tacheEnCoursEdition, dpDate.getValue())){
                    tacheEnCoursEdition.setDateDebut(dpDate.getValue());
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "La date ne respecte pas les règles du projet :\n" +
                                    "• Une sous-tâche ne peut pas commencer après sa tâche mère\n" +
                                    "• Une tâche mère ne peut pas commencer avant ses sous-tâches");
                    alert.setHeaderText("Date invalide");
                    alert.showAndWait();
                }

                try {
                    String dureeTxt = tfDuree.getText();
                    if (dureeTxt != null && !dureeTxt.trim().isEmpty()) {
                        int duree = Integer.parseInt(dureeTxt.trim());
                        tacheEnCoursEdition.setDureeEstimee(duree);
                    } else {
                        tacheEnCoursEdition.setDureeEstimee(1);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format pour la durée");
                }

                return tacheEnCoursEdition;
            }
            return null;
        });
    }

    private void updateAffichageEtiquettes(FlowPane pane) {
        pane.getChildren().clear();
        TacheAbstraite current = this.tacheEnCoursEdition;

        while (current instanceof TacheDecorateur) {
            if (current instanceof Etiquette) {
                Etiquette et = (Etiquette) current;

                HBox tagBox = new HBox(5);
                tagBox.setAlignment(Pos.CENTER_LEFT);
                tagBox.setStyle("-fx-background-color: " + et.getCouleur() + "; -fx-background-radius: 15; -fx-padding: 3 8;");

                Label lblNom = new Label(et.getLibelle());
                lblNom.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                Button btnSuppr = new Button("X");

                btnSuppr.setOnAction(e -> {
                    supprimerEtiquette(et, pane);
                });

                tagBox.getChildren().addAll(lblNom, btnSuppr);

                pane.getChildren().add(tagBox);
            }
            current = ((TacheDecorateur) current).getTacheDecoree();
        }
    }

    private void supprimerEtiquette(Etiquette etiquetteASupprimer, FlowPane pane) {
        TacheAbstraite t = tacheEnCoursEdition;
        while (t instanceof TacheDecorateur) {
            t = ((TacheDecorateur) t).getTacheDecoree();
        }

        List<Etiquette> aGarder = new ArrayList<>();
        TacheAbstraite current = tacheEnCoursEdition;
        while (current instanceof TacheDecorateur) {
            if (current instanceof Etiquette) {
                Etiquette e = (Etiquette) current;
                if (e != etiquetteASupprimer) {
                    aGarder.add(e);
                }
            }
            current = ((TacheDecorateur) current).getTacheDecoree();
        }

        this.tacheEnCoursEdition = t;

        for (Etiquette info : aGarder) {
            Etiquette nouvelleCouche = new Etiquette(this.tacheEnCoursEdition, info.getLibelle(), info.getCouleur());
            nouvelleCouche.setId(info.getId());

            this.tacheEnCoursEdition = nouvelleCouche;
        }
        updateAffichageEtiquettes(pane);
    }


}