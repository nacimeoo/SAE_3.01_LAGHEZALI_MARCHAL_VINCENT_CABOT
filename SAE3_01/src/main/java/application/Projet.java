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

    public void ajouterColonne(Colonne colonne){
        if (colonne != null){
            colonnes.add(colonne);
        }
    }

    public void supprimerColonne(int idColonne){
        colonnes.remove(idColonne);
    }

    public void afficher() {
        System.out.println("========================================================");
        System.out.println(" PROJET #" + id + ": " + nom + " : " + dateCreation);
        System.out.println("========================================================");

        if (colonnes.isEmpty()) {
            System.out.println("(Aucune colonne)");
        } else {
            for (Colonne colonne : colonnes) {
                colonne.afficher();
            }
        }
    }


}
