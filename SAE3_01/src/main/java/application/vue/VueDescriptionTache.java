package application.vue;

import application.Etiquette;
import application.TacheAbstraite;
import application.TacheDecorateur;
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

    public VueDescriptionTache(TacheAbstraite tache) {
        this.tacheEnCoursEdition = tache;

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

        grid.add(new Label("Etiquettes:"), 0, 3);
        grid.add(zoneEtiquettes, 1, 3);
        grid.add(new Label("Nouvelle:"), 0, 4);
        grid.add(boxAjoutEtiquette, 1, 4);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                tacheEnCoursEdition.setNom(tfTitre.getText());
                tacheEnCoursEdition.setDescription(taDesc.getText());
                tacheEnCoursEdition.setEtat(cbEtat.getValue());
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