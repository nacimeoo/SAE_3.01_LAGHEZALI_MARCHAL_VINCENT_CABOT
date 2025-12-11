package application;

import java.util.ArrayList;
import java.util.Date;

public class Projet {
    private int id;
    private String nom;
    private Date dateCreation;
    private ArrayList<Colonne> colonnes;

    public Projet(int id, String nom, Date dateCreation) {
        this.id = id;
        this.nom = nom;
        this.dateCreation = dateCreation;
        this.colonnes = new ArrayList<>();
    }

    public ArrayList<Colonne> getColonnes() {
        return colonnes;
    }

    public void ajouterColonne(Colonne colonne){
        if (colonne != null){
            colonnes.add(colonne);
        }
    }

    public void supprimerColonne(int idColonne){
        colonnes.remove(idColonne);
    }

    public void deplacerTache(int idColonneSource, int idColonneDestination, TacheAbstraite tache) {
        Colonne colonneSource = colonnes.get(idColonneSource);
        Colonne colonneDestination = colonnes.get(idColonneDestination);
        if (colonneSource != null && colonneDestination != null && tache != null) {
            colonneSource.supprimerTache(tache);
            colonneDestination.ajouterTache(tache);
        }
    }

    public ArrayList<TacheAbstraite> getTache() {
        ArrayList<TacheAbstraite> tache = new ArrayList<>();
        for (int i = 0; i < colonnes.size(); i++) {
            Colonne colonne = colonnes.get(i);
            for (TacheAbstraite t : colonne.getTaches()){
                tache.add(t);
            }
        }
        return tache;
    }

    public void afficher(String indient) {
        System.out.println("========================================================");
        System.out.println(" PROJET #" + id + ": " + nom + " : " + dateCreation);
        System.out.println("========================================================");

        if (colonnes.isEmpty()) {
            System.out.println("(Aucune colonne)");
        } else {
            for (Colonne colonne : colonnes) {
                System.out.println(colonne.afficher(indient));
            }
        }
    }


}
