package application.vue;

import application.Etiquette;
import application.TacheAbstraite;
import application.TacheDecorateur;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

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
                String webColor = toHexString(colorPicker.getValue());
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
                Label tag = new Label(et.getLibelle());
                tag.setStyle("-fx-background-color: " + et.getCouleur().replace("0x", "#") + "; -fx-text-fill: white; -fx-padding: 3; -fx-background-radius: 3;");
                pane.getChildren().add(tag);
            }
            current = ((TacheDecorateur) current).getTacheDecoree();
        }
    }

    private String toHexString(Color c) {
        return String.format( "#%02X%02X%02X",
                (int)( c.getRed() * 255 ),
                (int)( c.getGreen() * 255 ),
                (int)( c.getBlue() * 255 ) );
    }
}