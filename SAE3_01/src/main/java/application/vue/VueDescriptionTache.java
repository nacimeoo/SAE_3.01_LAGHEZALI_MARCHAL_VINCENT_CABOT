package application.vue;

import application.TacheAbstraite;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class VueDescriptionTache extends Dialog<TacheAbstraite> {

    public VueDescriptionTache(TacheAbstraite tache) {
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

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(tfTitre, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(taDesc, 1, 1);
        grid.add(new Label("Etat:"), 0, 2);
        grid.add(cbEtat, 1, 2);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                tache.setNom(tfTitre.getText());
                tache.setDescription(taDesc.getText());
                tache.setEtat(cbEtat.getValue());
                return tache;
            }
            return null;
        });
    }
}