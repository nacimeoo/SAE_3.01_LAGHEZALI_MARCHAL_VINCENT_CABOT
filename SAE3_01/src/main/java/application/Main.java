package application;

import application.vue.VueTexte;

public class Main {
    public static void main(String[] args) {

        Projet projet = null;
        VueTexte vue = new VueTexte();

        ControleurTexte controleur = new ControleurTexte(projet, vue);

        controleur.demarrer();
    }
}