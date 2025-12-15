package application.vue;

import application.Observateur;
import application.Projet;
import application.Sujet;
import application.TacheAbstraite;

import java.util.Scanner;

public class VueTexte implements Observateur {

    private Scanner sc;

    public VueTexte() {
        this.sc = new Scanner(System.in);
    }

    public int afficherMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1 - Créer un nouveau projet");
        System.out.println("2 - Créer une nouvelle colonne");
        System.out.println("3 - Créer une tâche");
        System.out.println("4 - Sélectionner tâche");
        System.out.println("5 - Supprimer colonne");
        System.out.println("6 - Voir projet");
        System.out.println("0 - Quitter");
        System.out.print("Votre choix : ");
        return lireEntier();
    }

    public int lireEntier() {
        try {
            int choix = sc.nextInt();
            sc.nextLine(); // Consommer le saut de ligne
            return choix;
        } catch (Exception e) {
            sc.nextLine();
            return -1;
        }
    }

    public int lireEntier(String message) {
        System.out.print(message + " : ");
        return lireEntier();
    }



    public String lireChaine(String message) {
        System.out.print(message + " : ");
        return sc.nextLine();
    }

    public void afficherMessage(String message) {
        System.out.println(message);
    }

    public void afficherTaches(Projet p, int colIndex) {
        for (int i = 0; i < p.getColonnes().get(colIndex).getTaches().size(); i++) {
            System.out.println(i + " : " + p.getColonnes().get(colIndex).getTaches().get(i).getNom());
        }
    }
    public void afficherProjet(Projet p) {
        if (p == null) {
            System.out.println("⚠ Aucun projet n'a encore été créé.");
            return;
        }
        p.afficher("  ");
    }


    public void afficherColonnes(Projet p) {
        if (p == null) return;
        for (int i = 0; i < p.getColonnes().size(); i++) {
            System.out.println(i + " : " + p.getColonnes().get(i).getNom());
        }
    }
    @Override
    public void actualiser(Sujet s) {
        if(s instanceof TacheAbstraite) {
            System.out.println("Mise à jour de la tâche : " + ((TacheAbstraite)s).getNom());
        }

    }
}
