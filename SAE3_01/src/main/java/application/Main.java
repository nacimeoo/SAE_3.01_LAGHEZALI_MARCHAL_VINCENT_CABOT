package application;

import application.vue.VueTexte;

public class Main {
    public static void main(String[] args) throws Exception {

        Projet projet = null;
        ProjetService projetService = new ProjetService();
        VueTexte vue = new VueTexte();

        ControleurTexte controleur = new ControleurTexte(projetService, projet, vue);

        controleur.demarrer();
    }
}