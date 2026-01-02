package application.controller;

import application.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurRetourDashboard implements EventHandler<ActionEvent> {

    private MainApp mainApp;

    public ControleurRetourDashboard(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void handle(ActionEvent event) {
        mainApp.afficherDashboard();
    }
}