package application;

import java.util.Date;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("===========================================");
        System.out.println("=========== BIENVENU SUR FRIDAY ===========");
        System.out.println("===========================================\n");

        Scanner sc = new Scanner(System.in);
        Projet projet = null;

        boolean run = true;
        while (run) {

            System.out.println("\nMenu :");
            System.out.println("1 - Créer un nouveau projet");
            System.out.println("2 - Créer une nouvelle colonne");
            System.out.println("3 - Créer une tâche");
            System.out.println("4 - Sélectionner tâche");
            System.out.println("5 - Supprimer colonne");
            System.out.println("6 - Voir projet");
            System.out.println("0 - Quitter");

            int action = sc.nextInt();
            sc.nextLine();

            switch (action) {

                case 1:
                    System.out.print("Nom du projet : ");
                    String nom = sc.nextLine();
                    projet = new Projet(1, nom, new Date());
                    System.out.println("Projet créé.");
                    break;


                case 2:
                    if (projet == null) { msgNoProject(); break; }
                    System.out.print("Nom de la colonne : ");
                    String nomCol = sc.nextLine();
                    projet.ajouterColonne(new Colonne(nomCol));
                    System.out.println("Colonne ajoutée.");
                    break;


                case 3:
                    if (projet == null) { msgNoProject(); break; }

                    System.out.println("1 - Tâche mère");
                    System.out.println("2 - Sous-tâche");
                    int type = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Nom de la tâche : ");
                    String nomT = sc.nextLine();

                    afficherColonnes(projet);
                    System.out.print("Index colonne : ");
                    int c = sc.nextInt();
                    sc.nextLine();

                    TacheAbstraite t = (type == 1)
                            ? new TacheMere((int)(Math.random()*1000), nomT)
                            : new SousTache((int)(Math.random()*1000), nomT);

                    projet.getColonnes().get(c).ajouterTache(t);
                    System.out.println("Tâche ajoutée.");
                    break;

                case 4:
                    if (projet == null) { msgNoProject(); break; }
                    System.out.println("Sélection d'une tâche :");
                    afficherColonnes(projet);
                    System.out.print("Colonne de la tâche : ");
                    int colSel = sc.nextInt();

                    int NbTache = projet.getColonnes().get(colSel).getTaches().size();
                    if (NbTache == 0) {
                        System.out.println("Il n'y a pas de tâche assigné à cette colonne");; break; }

                    afficherTaches(projet, colSel);
                    System.out.print("Index tâche : ");
                    int tSel = sc.nextInt();
                    sc.nextLine();

                    TacheAbstraite tacheSelectionnee = projet.getColonnes().get(colSel).getTaches().get(tSel);

                    boolean menuTache = true;
                    while (menuTache) {
                        System.out.println("\n--- Actions sur la tâche \"" + tacheSelectionnee.getNom() + "\" ---");
                        System.out.println("1 - Ajouter une dépendance (si tâche mère)");
                        System.out.println("2 - Déplacer la tâche vers une autre colonne");
                        System.out.println("3 - Supprimer la tâche");
                        System.out.println("4 - Changer l'état de la tâche");
                        System.out.println("5 - Voir état");
                        System.out.println("0 - Retour");

                        int choixT = sc.nextInt();
                        sc.nextLine();

                        switch (choixT) {

                            case 1:
                                if (!(tacheSelectionnee instanceof TacheMere)) {
                                    System.out.println("Cette tâche n'est pas une tâche mère !");
                                    break;
                                }

                                int cpt = 0;
                                for (TacheAbstraite tache : projet.getTache()) {
                                    if (tache instanceof SousTache){
                                        cpt++;
                                    }
                                }
                                if (cpt == 0) {
                                    System.out.println("Il n'y a pas de SousTache crée");
                                    break;
                                }
                                System.out.println("Sélectionner la sous-tâche à ajouter :");
                                afficherColonnes(projet);
                                int colDep = sc.nextInt();

                                afficherTaches(projet, colDep);
                                int tDep = sc.nextInt();
                                sc.nextLine();

                                TacheAbstraite sousT = projet.getColonnes().get(colDep).getTaches().get(tDep);
                                ((TacheMere) tacheSelectionnee).ajouterDependance(sousT);

                                System.out.println("Dépendance ajoutée !");
                                break;


                            case 2:
                                System.out.println("Déplacer vers quelle colonne ?");
                                afficherColonnes(projet);
                                int newCol = sc.nextInt();
                                sc.nextLine();

                                projet.deplacerTache(colSel, newCol, tacheSelectionnee);
                                System.out.println("Tâche déplacée !");
                                menuTache = false;
                                break;


                            case 3:
                                projet.getColonnes().get(colSel).supprimerTache(tacheSelectionnee);
                                System.out.println("Tâche supprimée !");
                                menuTache = false;
                                break;

                            case 4:
                                boolean menuEtat = true;
                                while (menuEtat) {
                                    System.out.println("\n--- Changement de l'état de la tâche \"" + tacheSelectionnee.getNom() + " qui est " + tacheSelectionnee.getEtat() +"\" ---");
                                    System.out.println("1 - A faire");
                                    System.out.println("2 - En cours");
                                    System.out.println("3 - Terminer");
                                    System.out.println("4 - En attente");
                                    System.out.println("0 - Retour");

                                    int choixEtat = sc.nextInt();

                                    switch (choixEtat) {
                                        case 1:
                                            tacheSelectionnee.setEtat("A faire");
                                            menuEtat = false;
                                            break;
                                        case 2:
                                            if (!tacheSelectionnee.verifierDependance()) {
                                                System.out.println("Attention cette tâche nécéssite la finission d'une autre tâche avant");
                                                projet.afficher("   ");
                                            }
                                            tacheSelectionnee.setEtat("En cours");
                                            menuEtat = false;
                                            break;
                                        case 3:
                                            tacheSelectionnee.setEtat("Terminer");
                                            menuEtat = false;
                                            break;
                                        case 4:
                                            tacheSelectionnee.setEtat("En attente");
                                            menuEtat = false;
                                            break;
                                        default:
                                            System.out.println("Option invalide.");
                                            break;
                                    }

                                }
                                System.out.println("L'état à bien été modifié");
                                break;
                            case 5:
                                System.out.println("L'état de la tâche est : " + tacheSelectionnee.getEtat());
                                break;
                            case 0:
                                menuTache = false;
                                break;

                            default:
                                System.out.println("Option invalide.");
                        }
                    }

                    break;


                case 5:
                    if (projet == null) { msgNoProject(); break; }
                    afficherColonnes(projet);
                    System.out.print("Index colonne à supprimer : ");
                    int delC = sc.nextInt();
                    projet.supprimerColonne(delC);
                    System.out.println("Colonne supprimée.");
                    break;


                case 6:
                    if (projet == null) { msgNoProject(); break; }
                    String indient = "  ";
                    projet.afficher(indient);
                    break;


                case 0:
                    run = false;
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Option invalide.");
            }
        }
    }

    private static void afficherColonnes(Projet p) {
        for (int i = 0; i < p.getColonnes().size(); i++) {
            System.out.println(i + " : " + p.getColonnes().get(i).getNom());
        }
    }

    private static void afficherTaches(Projet p, int colIndex) {
        for (int i = 0; i < p.getColonnes().get(colIndex).getTaches().size(); i++) {
            System.out.println(i + " : " + p.getColonnes().get(colIndex).getTaches().get(i).getNom());
        }
    }

    private static void msgNoProject() {
        System.out.println("⚠ Aucun projet n’a encore été créé.");
    }
}
