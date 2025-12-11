package application;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
        System.out.println("--- Création du Projet ---");
        Projet monProjet = new Projet(1, "Gestion de Tâches Agile", new Date());

        System.out.println("--- Création des Colonnes ---");
        Colonne colTodo = new Colonne("À Faire");
        Colonne colInProgress = new Colonne("En Cours");
        Colonne colDone = new Colonne("Terminé");

        monProjet.ajouterColonne(colTodo);
        monProjet.ajouterColonne(colInProgress);
        monProjet.ajouterColonne(colDone);

        System.out.println("--- Création des Tâches ---");

        TacheMere grosseTache = new TacheMere(100, "Développer le Backend");
        grosseTache.setDescription("Architecture et API");
        grosseTache.setPriorite(1);
        grosseTache.setDureeEstimee(20);

        SousTache sousTache1 = new SousTache(101, "Créer la base de données");
        SousTache sousTache2 = new SousTache(102, "Faire les contrôleurs REST");

        System.out.println("--- Ajout des dépendances ---");
        boolean ajoutDep1 = grosseTache.ajouterDependance(sousTache1);
        boolean ajoutDep2 = grosseTache.ajouterDependance(sousTache2);

        boolean ajoutInvalide = sousTache1.ajouterDependance(grosseTache);

        System.out.println("Dépendance 1 ajoutée : " + ajoutDep1);
        System.out.println("Dépendance invalide ajoutée : " + ajoutInvalide);

        System.out.println("--- Ajout des tâches dans les colonnes ---");
        try {
            colTodo.ajouterTache(grosseTache);
            colTodo.ajouterTache(sousTache1);
            colInProgress.ajouterTache(sousTache2);
        } catch (NullPointerException e) {
            System.err.println("ERREUR : La liste des tâches dans 'Colonne' n'est pas initialisée !");
        }

        System.out.println("\n--- AFFICHAGE FINAL ---");
        monProjet.afficher();
    }
}