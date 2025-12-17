package application;

import application.vue.VueTexte;
import java.util.Date;

public class ControleurTexte {

    private ProjetService projetService;
    private Projet projet;
    private VueTexte vue;
    private boolean run;

    public ControleurTexte(ProjetService projetService,Projet projet, VueTexte vue) {
        this.projet = projet;
        this.projetService = projetService;
        this.vue = vue;
        this.run = true;
    }

    public void demarrer() throws Exception {
        vue.afficherMessage("===========================================");
        vue.afficherMessage("=========== BIENVENU SUR FRIDAY ===========");
        vue.afficherMessage("===========================================\n");

        while (run) {
            int choix = vue.afficherMenu();
            traiterAction(choix);
        }
    }

    private void traiterAction(int action) throws Exception {
        switch (action) {
            case 1:
                String nom = vue.lireChaine("Nom du projet");
                this.projet = new Projet(1, nom, new Date());
                this.projet.enregistrerObservateur(this.vue);
                vue.afficherMessage("Projet créé avec succès.");
                break;

            case 2:
                if (projetExiste()) {
                    String nomCol = vue.lireChaine("Nom de la colonne");
                    projetService.ajouterColonne(projet,new Colonne(nomCol));
                }
                break;

            case 3:
                if (projetExiste()) {
                    vue.afficherMessage("1 - Tâche mère\n2 - Sous-tâche");
                    int type = vue.lireEntier();
                    String nomT = vue.lireChaine("Nom de la tâche");

                    vue.afficherMessage("Sélectionnez une colonne :");
                    vue.afficherColonnes(projet);
                    int c = vue.lireEntier("Index colonne");

                    TacheAbstraite t = (type == 1)
                            ? new TacheMere((int)(Math.random()*1000), nomT)
                            : new SousTache((int)(Math.random()*1000), nomT);

                    projetService.ajouterTache(projet.trouverColonneParId(c),t);
                }
                break;

            case 4:
                if (projetExiste()) {
                    gererMenuTache();
                }
                break;

            case 5:
                if (projetExiste()) {
                    vue.afficherColonnes(projet);
                    int c = vue.lireEntier("Index colonne à supprimer");
                    projetService.supprimerColonne(projet, projet.trouverColonneParId(c));
                }
                break;

            case 6:
                if (projetExiste()) {
                    projet.afficher("  ");
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

    private void gererMenuTache() throws Exception {
        vue.afficherColonnes(projet);
        int colSel = vue.lireEntier("Choisir colonne");

        if (!indexValide(colSel)) { vue.afficherMessage("Index colonne invalide"); return; }
        if (projet.getColonnes().get(colSel).getTaches().isEmpty()) { vue.afficherMessage("Aucune tâche ici."); return; }

        vue.afficherTaches(projet, colSel);
        int tSel = vue.lireEntier("Choisir tâche");

        if (tSel < 0 || tSel >= projet.getColonnes().get(colSel).getTaches().size()) {
            vue.afficherMessage("Index tâche invalide");
            return;
        }

        TacheAbstraite laTache = projet.getColonnes().get(colSel).getTaches().get(tSel);
        boolean sousMenu = true;

        while (sousMenu) {
            vue.afficherMessage("\n--- TACHE : " + laTache.getNom() + " (" + laTache.getEtat() + ") ---");
            vue.afficherMessage("1 - Ajouter dépendance (si Mère)");
            vue.afficherMessage("2 - Déplacer");
            vue.afficherMessage("3 - Supprimer");
            vue.afficherMessage("4 - Changer état");
            vue.afficherMessage("0 - Retour");

            int choix = vue.lireEntier();

            switch (choix) {
                case 1:
                    if (laTache instanceof TacheMere) {
                        ajouterDependance((TacheMere) laTache);
                    } else {
                        vue.afficherMessage("Impossible : ce n'est pas une tâche mère.");
                    }
                    break;
                case 2:
                    vue.afficherColonnes(projet);
                    int newCol = vue.lireEntier("Vers colonne index");
                    projetService.deplacerTache(projet.trouverColonneParId(colSel), projet.trouverColonneParId(newCol), laTache);
                    sousMenu = false; // On sort car l'index a changé
                    break;
                case 3:
                    projetService.supprimerTache(projet.trouverColonneParId(colSel),laTache);
                    sousMenu = false;
                    break;
                case 4:
                    changerEtat(laTache);
                    break;
                case 0:
                    sousMenu = false;
                    break;
                default:
                    vue.afficherMessage("Invalide.");
            }
        }
    }

    private void ajouterDependance(TacheMere mere) throws Exception {
        vue.afficherMessage("Selectionnez la colonne de la sous-tâche cible :");
        vue.afficherColonnes(projet);
        int c = vue.lireEntier();
        if(!indexValide(c)) return;

        vue.afficherTaches(projet, c);
        int t = vue.lireEntier("Index de la sous-tâche");

        var col = projet.getColonnes().get(c);
        if (t >= 0 && t < col.getTaches().size()) {
            TacheAbstraite cible = col.getTaches().get(t);
            projetService.ajouterDependance(projet,mere, cible);
            vue.afficherMessage("Dépendance ajoutée.");
        }
    }

        private void changerEtat(TacheAbstraite t) throws Exception {
        vue.afficherMessage("1- A faire, 2- En cours, 3- Terminer, 4- En attente");
        int e = vue.lireEntier();
        String etat = switch(e) {
            case 1 -> "A faire";
            case 2 -> "En cours";
            case 3 -> "Terminer";
            case 4 -> "En attente";
            default -> null;
        };

        if (etat != null) {
            if (etat.equals("En cours") && !t.verifierDependance()) {
                vue.afficherMessage("⚠ Attention : Dépendances non terminées !");
            }
            projetService.changerEtat(projet,t, etat);
        }
    }

    private boolean projetExiste() {
        if (projet == null) {
            vue.afficherMessage("⚠ Aucun projet créé.");
            return false;
        }
        return true;
    }

    private boolean indexValide(int i) {
        return projet != null && i >= 0 && i < projet.getColonnes().size();
    }
}