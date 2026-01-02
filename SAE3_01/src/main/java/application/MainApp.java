package application;

import application.controller.ControleurRetourDashboard;
import application.vue.VueDashboard;
import application.vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private ProjetService projetService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("FRIDAY");
        this.projetService = new ProjetService();

        afficherDashboard();
        this.primaryStage.show();
    }

    public void afficherDashboard() {
        VueDashboard dashboard = new VueDashboard(this);
        Scene scene = new Scene(dashboard, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FRIDAY - Dashboard");
    }

    public void afficherKanban(Projet projetSelectionne) {
        Projet projetComplet = projetService.chargerProjetComplet(projetSelectionne.getId());
        if (projetComplet != null) {
            VueKanban kanban = new VueKanban(projetComplet, projetService);
            if (kanban.getTop() instanceof HBox) {
                HBox header = (HBox) kanban.getTop();
                if (!header.getChildren().isEmpty() && header.getChildren().get(0) instanceof Button) {
                    Button btnBack = (Button) header.getChildren().get(0);
                    btnBack.setOnAction(new ControleurRetourDashboard(this));
                }
            }

            Scene scene = new Scene(kanban, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FRIDAY - " + projetComplet.getNom());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}