package application;

import application.vue.VueTexte;
import java.util.Date;

public class ControleurTexte {

    private Projet projet;
    private VueTexte vue;
    private boolean run;

    /**
     * Constructeur du contrôleur.
     * @param projet Le modèle (peut être null au départ)
     * @param vue La vue pour les interactions
     */
    public ControleurTexte(Projet projet, VueTexte vue) {
        this.projet = projet;
        this.vue = vue;
        this.run = true;
    }

    /**
     * Méthode principale qui lance la boucle de l'application.
     */
    public void demarrer() {
        vue.afficherMessage("===========================================");
        vue.afficherMessage("=========== BIENVENU SUR FRIDAY ===========");
        vue.afficherMessage("===========================================\n");

        while (run) {
            int action = vue.afficherMenu();
            traiterAction(action);
        }
    }

    /**
     * Traite le choix de l'utilisateur dans le menu principal.
     */
    private void traiterAction(int action) {
        switch (action) {
            case 1:
                String nom = vue.lireChaine("Nom du projet");
                this.projet = new Projet(1, nom, new Date());
                vue.afficherMessage("Projet créé.");
                break;

            case 2:
                if (verifierProjetExistant()) {
                    String nomCol = vue.lireChaine("Nom de la colonne");
                    projet.ajouterColonne(new Colonne(nomCol));
                    vue.afficherMessage("Colonne ajoutée.");
                }
                break;

            case 3:
                if (verifierProjetExistant()) {
                    vue.afficherMessage("1 - Tâche mère");
                    vue.afficherMessage("2 - Sous-tâche");
                    int type = vue.lireEntier();

                    String nomT = vue.lireChaine("Nom de la tâche");

                    vue.afficherColonnes(projet);
                    int c = vue.lireEntier("Index colonne");

                    if (indexColonneValide(c)) {
                        TacheAbstraite t = (type == 1)
                                ? new TacheMere((int)(Math.random()*1000), nomT)
                                : new SousTache((int)(Math.random()*1000), nomT);

                        projet.getColonnes().get(c).ajouterTache(t);
                        vue.afficherMessage("Tâche ajoutée.");
                    } else {
                        vue.afficherMessage("Index de colonne invalide.");
                    }
                }
                break;

            case 4:
                if (verifierProjetExistant()) {
                    gererSelectionTache();
                }
                break;

            case 5:
                if (verifierProjetExistant()) {
                    vue.afficherColonnes(projet);
                    int delC = vue.lireEntier("Index colonne à supprimer");
                    if (indexColonneValide(delC)) {
                        projet.supprimerColonne(delC);
                        vue.afficherMessage("Colonne supprimée.");
                    } else {
                        vue.afficherMessage("Index invalide.");
                    }
                }
                break;

            case 6:
                if (verifierProjetExistant()) {
                    vue.afficherProjet(projet);
                }
                break;

            case 0:
                run = false;
                vue.afficherMessage("Au revoir !");
                break;

            default:
                vue.afficherMessage("Option invalide.");
        }
    }

    /**
     * Gère toute la logique du menu "Sélectionner tâche" (Case 4).
     */
    private void gererSelectionTache() {
        vue.afficherMessage("Sélection d'une tâche :");
        vue.afficherColonnes(projet);
        int colSel = vue.lireEntier("Colonne de la tâche");

        if (!indexColonneValide(colSel)) {
            vue.afficherMessage("Colonne invalide.");
            return;
        }

        int nbTache = projet.getColonnes().get(colSel).getTaches().size();
        if (nbTache == 0) {
            vue.afficherMessage("Il n'y a pas de tâche assignée à cette colonne");
            return;
        }

        vue.afficherTaches(projet, colSel);
        int tSel = vue.lireEntier("Index tâche");

        if (tSel < 0 || tSel >= nbTache) {
            vue.afficherMessage("Index de tâche invalide.");
            return;
        }

        TacheAbstraite tacheSelectionnee = projet.getColonnes().get(colSel).getTaches().get(tSel);

        boolean menuTache = true;
        while (menuTache) {
            vue.afficherMessage("\n--- Actions sur la tâche \"" + tacheSelectionnee.getNom() + "\" ---");
            vue.afficherMessage("1 - Ajouter une dépendance (si tâche mère)");
            vue.afficherMessage("2 - Déplacer la tâche vers une autre colonne");
            vue.afficherMessage("3 - Supprimer la tâche");
            vue.afficherMessage("4 - Changer l'état de la tâche");
            vue.afficherMessage("5 - Voir état");
            vue.afficherMessage("0 - Retour");

            int choixT = vue.lireEntier();

            switch (choixT) {
                case 1:
                    if (!(tacheSelectionnee instanceof TacheMere)) {
                        vue.afficherMessage("Cette tâche n'est pas une tâche mère !");
                        break;
                    }

                    int cpt = 0;
                    for (TacheAbstraite tache : projet.getTache()) {
                        if (tache instanceof SousTache){
                            cpt++;
                        }
                    }
                    if (cpt == 0) {
                        vue.afficherMessage("Il n'y a pas de SousTache créée");
                        break;
                    }

                    vue.afficherMessage("Sélectionner la sous-tâche à ajouter :");
                    vue.afficherColonnes(projet);
                    int colDep = vue.lireEntier("Colonne source");

                    if (indexColonneValide(colDep)) {
                        vue.afficherTaches(projet, colDep);
                        int tDep = vue.lireEntier("Index tâche");

                        if (tDep >= 0 && tDep < projet.getColonnes().get(colDep).getTaches().size()) {
                            TacheAbstraite sousT = projet.getColonnes().get(colDep).getTaches().get(tDep);
                            ((TacheMere) tacheSelectionnee).ajouterDependance(sousT);
                            vue.afficherMessage("Dépendance ajoutée !");
                        } else {
                            vue.afficherMessage("Index tâche invalide.");
                        }
                    }
                    break;

                case 2:
                    vue.afficherMessage("Déplacer vers quelle colonne ?");
                    vue.afficherColonnes(projet);
                    int newCol = vue.lireEntier();

                    if (indexColonneValide(newCol)) {
                        projet.deplacerTache(colSel, newCol, tacheSelectionnee);
                        vue.afficherMessage("Tâche déplacée !");
                        menuTache = false;
                    }
                    break;

                case 3:
                    projet.getColonnes().get(colSel).supprimerTache(tacheSelectionnee);
                    vue.afficherMessage("Tâche supprimée !");
                    menuTache = false;
                    break;

                case 4:
                    gererChangementEtat(tacheSelectionnee);
                    break;

                case 5:
                    vue.afficherMessage("L'état de la tâche est : " + tacheSelectionnee.getEtat());
                    break;

                case 0:
                    menuTache = false;
                    break;

                default:
                    vue.afficherMessage("Option invalide.");
            }
        }
    }

    /**
     * Gère le sous-menu de changement d'état.
     */
    private void gererChangementEtat(TacheAbstraite tacheSelectionnee) {
        boolean menuEtat = true;
        while (menuEtat) {
            vue.afficherMessage("\n--- Changement de l'état de la tâche \"" + tacheSelectionnee.getNom() + " qui est " + tacheSelectionnee.getEtat() +"\" ---");
            vue.afficherMessage("1 - A faire");
            vue.afficherMessage("2 - En cours");
            vue.afficherMessage("3 - Terminer");
            vue.afficherMessage("4 - En attente");
            vue.afficherMessage("0 - Retour");

            int choixEtat = vue.lireEntier();

            switch (choixEtat) {
                case 1:
                    tacheSelectionnee.setEtat("A faire");
                    menuEtat = false;
                    break;
                case 2:
                    if (!tacheSelectionnee.verifierDependance()) {
                        vue.afficherMessage("Attention cette tâche nécessite la finission d'une autre tâche avant");
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
                case 0:
                    menuEtat = false;
                    break;
                default:
                    vue.afficherMessage("Option invalide.");
                    break;
            }
        }
        vue.afficherMessage("L'état a bien été modifié");
    }

    private boolean verifierProjetExistant() {
        if (projet == null) {
            vue.afficherMessage("⚠ Aucun projet n’a encore été créé.");
            return false;
        }
        return true;
    }

    private boolean indexColonneValide(int index) {
        return index >= 0 && index < projet.getColonnes().size();
    }
}