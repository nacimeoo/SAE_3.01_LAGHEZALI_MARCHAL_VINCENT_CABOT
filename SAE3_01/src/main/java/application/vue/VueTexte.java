package application.vue;

import application.Observateur;
import application.Projet;
import application.Sujet;

import java.util.Scanner;

public class VueTexte implements Observateur {

    private Scanner sc;

    public VueTexte() {
        this.sc = new Scanner(System.in);
    }

    public int afficherMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1 - Créer un nouveau projet");
        System.out.println("2 - Créer une nouvelle colonne");
        System.out.println("3 - Créer une tâche");
        System.out.println("4 - Sélectionner tâche (Actions)");
        System.out.println("5 - Supprimer colonne");
        System.out.println("6 - Forcer l'affichage du projet");
        System.out.println("0 - Quitter");
        return lireEntier("Votre choix");
    }

    public void afficherMessage(String msg) {
        System.out.println(msg);
    }

    public void afficherColonnes(Projet p) {
        if (p == null || p.getColonnes().isEmpty()) {
            System.out.println("(Aucune colonne disponible)");
            return;
        }
        for (int i = 0; i < p.getColonnes().size(); i++) {
            System.out.println(i + " : " + p.getColonnes().get(i).getNom());
        }
    }

    public void afficherTaches(Projet p, int colIndex) {
        if (colIndex < 0 || colIndex >= p.getColonnes().size()) return;

        var taches = p.getColonnes().get(colIndex).getTaches();
        if (taches.isEmpty()) {
            System.out.println("(Aucune tâche dans cette colonne)");
        } else {
            for (int i = 0; i < taches.size(); i++) {
                System.out.println(i + " : " + taches.get(i).getNom() + " [" + taches.get(i).getEtat() + "]");
            }
        }
    }


    public String lireChaine(String label) {
        System.out.print(label + " : ");
        return sc.nextLine();
    }

    public int lireEntier(String label) {
        System.out.print(label + " : ");
        try {
            int i = sc.nextInt();
            sc.nextLine();
            return i;
        } catch (Exception e) {
            sc.nextLine();
            return -1;
        }
    }

    public int lireEntier() {
        return lireEntier("Choix");
    }


    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Projet) {
            ((Projet) s).afficher("  ");
        }
    }
}