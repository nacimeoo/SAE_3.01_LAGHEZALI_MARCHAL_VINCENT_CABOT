package application.vue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VueDashboard extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titrelabel = new Label("DashBord");
        titrelabel.setFont(new Font("Arial", 30));

        VBox projetsSection = new VBox(10);
        projetsSection.setPadding(new Insets(15));

        projetsSection.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1)
        )));

        Label projetTitre = new Label("Mes Projets");
        projetTitre.setFont(new Font("Arial", 18));

        FlowPane projetsContainer = new FlowPane();
        projetsContainer.setHgap(20);
        projetsContainer.setVgap(20);
        projetsContainer.setPrefHeight(200);

        for (int i = 1; i <= 4; i++) {
            Button btnProjet = new Button("Projet " + i);
            btnProjet.setPrefSize(100, 60);
            projetsContainer.getChildren().add(btnProjet);
        }

        projetsSection.getChildren().addAll(projetTitre, projetsContainer);

        HBox bottomControls = new HBox(20);
        bottomControls.setAlignment(Pos.CENTER);

        TextField nomInput = new TextField();
        nomInput.setPromptText("Nom du projet");

        Button btnAdd = new Button("Ajouter");
        Button btnDelete = new Button("Supprimer");
        Button btnOpen = new Button("Ouvrir");

        bottomControls.getChildren().addAll(nomInput, btnAdd, btnOpen, btnDelete);

        root.getChildren().addAll(titrelabel, projetsSection, bottomControls);

        Scene scene = new Scene(root, 600, 450);
        primaryStage.setTitle("Window Name");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}