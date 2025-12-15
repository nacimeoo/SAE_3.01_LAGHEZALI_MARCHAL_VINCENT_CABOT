package application;

import application.vue.VueTexte;

public class Main {
    public static void main(String[] args) {
        VueTexte vue = new VueTexte();

        Projet projet = null;

        ControleurTexte controleur = new ControleurTexte(projet, vue);

        controleur.demarrer();
    }
}