package application;

import application.vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Date;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialisation des Données (Modèle)
        Projet projet = new Projet( "Mon Projet", new Date());

        // Ajout de colonnes par défaut
        Colonne todo = new Colonne("A faire");
        Colonne doing = new Colonne("En cours");
        Colonne done = new Colonne("Terminé");

        projet.ajouterColonne(todo);
        projet.ajouterColonne(doing);
        projet.ajouterColonne(done);

        // 3. Initialisation de la Vue (MVC)
        VueKanban root = new VueKanban(projet);

        // 4. Lancement JavaFX
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("FRIDAY");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}