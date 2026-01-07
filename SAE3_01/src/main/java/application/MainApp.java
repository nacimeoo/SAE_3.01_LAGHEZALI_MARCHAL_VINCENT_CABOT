package application;

import application.controller.ControleurRetourDashboard;
import application.vue.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
                for (javafx.scene.Node n : header.getChildren()) {
                    if (n instanceof Button b) {
                        if ("<- Dashboard".equals(b.getText())) {
                            b.setOnAction(new ControleurRetourDashboard(this));
                        }
                    }
                }
            }

            if (kanban.getRight() instanceof VBox) {
                VBox sideBar = (VBox) kanban.getRight();
                for (javafx.scene.Node n : sideBar.getChildren()) {
                    if (n instanceof Button b) {
                        if ("Vue Liste".equals(b.getText())) {
                            b.setOnAction(e -> afficherListe(projetComplet));
                        }else if ("Vue Gantt".equals(b.getText())) {
                            b.setOnAction(e -> afficherGantt(projetComplet));
                        }
                        else if ("Voir Archives".equals(b.getText())) {
                            b.setOnAction(e -> afficherArchives(projetComplet));
                        }
                    }
                }
            }
            Scene scene = new Scene(kanban, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FRIDAY - " + projetComplet.getNom());
        }
    }

    public void afficherListe(Projet projetSelectionne) {
        Projet projetComplet = projetService.chargerProjetComplet(projetSelectionne.getId());
        if (projetComplet != null) {
            VueListe vueListe = new VueListe(projetComplet, projetService);


            if (vueListe.getTop() instanceof HBox) {
                HBox header = (HBox) vueListe.getTop();
                if (!header.getChildren().isEmpty() && header.getChildren().get(0) instanceof Button) {
                    Button btnBack = (Button) header.getChildren().get(0);
                    btnBack.setOnAction(new ControleurRetourDashboard(this));
                }
            }

            if (vueListe.getTop() instanceof HBox) {
                HBox header = (HBox) vueListe.getTop();
                for (javafx.scene.Node n : header.getChildren()) {
                    if (n instanceof Button b) {
                        if ("<- Dashboard".equals(b.getText())) {
                            b.setOnAction(new ControleurRetourDashboard(this));
                        }
                    }
                }
            }
            if (vueListe.getRight() instanceof VBox) {
                VBox sideBar = (VBox) vueListe.getRight();
                for (javafx.scene.Node n : sideBar.getChildren()) {
                    if (n instanceof Button b) {
                        if ("Vue Kanban".equals(b.getText())) {
                            b.setOnAction(e -> afficherKanban(projetComplet));
                        }else if ("Vue Gantt".equals(b.getText())) {
                            b.setOnAction(e -> afficherGantt(projetComplet));
                        }
                    }
                }
            }

            Scene scene = new Scene(vueListe, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FRIDAY - Liste " + projetComplet.getNom());
        }
    }

    public void afficherGantt(Projet projetSelectionne) {
        Projet projetComplet = projetService.chargerProjetComplet(projetSelectionne.getId());
        if (projetComplet != null) {

            VueGantt vueGantt = new VueGantt(projetComplet, projetService);
            if (vueGantt.getTop() instanceof HBox) {
                HBox header = (HBox) vueGantt.getTop();
                if (!header.getChildren().isEmpty() && header.getChildren().get(0) instanceof Button) {
                    Button btnBack = (Button) header.getChildren().get(0);
                    btnBack.setOnAction(new ControleurRetourDashboard(this));
                }
            }

            if (vueGantt.getTop() instanceof HBox) {
                HBox header = (HBox) vueGantt.getTop();
                for (javafx.scene.Node n : header.getChildren()) {
                    if (n instanceof Button b) {
                        if ("<- Dashboard".equals(b.getText())) {
                            b.setOnAction(new ControleurRetourDashboard(this));
                        }
                    }
                }
            }
            if (vueGantt.getRight() instanceof VBox) {
                VBox sideBar = (VBox) vueGantt.getRight();
                for (javafx.scene.Node n : sideBar.getChildren()) {
                    if (n instanceof Button b) {
                        if ("Vue Kanban".equals(b.getText())) {
                            b.setOnAction(e -> afficherKanban(projetComplet));
                        }else if ("Vue Liste".equals(b.getText())) {
                            b.setOnAction(e -> afficherListe(projetComplet));
                        } else if ("Voir Archives".equals(b.getText())) {
                            b.setOnAction(e -> afficherArchives(projetComplet));
                        }
                    }
                }
            }
            Scene scene = new Scene(vueGantt, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FRIDAY - Liste " + projetComplet.getNom());
        }
    }

    public void afficherArchives(Projet projetSelectionne) {
        Projet projetComplet = projetService.chargerProjetComplet(projetSelectionne.getId());
        if (projetComplet != null) {
            VueArchive vueArchives = new VueArchive(projetComplet, projetService);

            if (vueArchives.getTop() instanceof HBox) {
                HBox header = (HBox) vueArchives.getTop();
                for (javafx.scene.Node n : header.getChildren()) {
                    if (n instanceof Button b) {
                        if ("<- Retour Kanban".equals(b.getText())) {
                            b.setOnAction(e -> afficherKanban(projetComplet));
                        }
                    }
                }
            }

            Scene scene = new Scene(vueArchives, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FRIDAY - Archives " + projetComplet.getNom());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}