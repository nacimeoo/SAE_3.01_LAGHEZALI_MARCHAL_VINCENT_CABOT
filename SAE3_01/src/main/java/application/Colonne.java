package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Colonne {

    private int id;
    private String nom;
    private List<TacheAbstraite> taches = new ArrayList<>();

    public Colonne(String nom) {
        this.nom = nom;
    }

    public Colonne(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public List<TacheAbstraite> getTaches() {
        return taches;
    }

    public void ajouterTache(TacheAbstraite tache) {
        if (tache != null) taches.add(tache);
    }

    public void supprimerTache(TacheAbstraite tache) {
        taches.remove(tache);
    }

    public String afficher(String indent) {
        List<TacheAbstraite> dependances = new ArrayList<>();

        for (TacheAbstraite t : taches) {
            if (t instanceof TacheMere) {
                dependances.addAll(((TacheMere) t).dependance);
            }
        }

        StringBuilder sb = new StringBuilder("--- Colonne : " + nom + " ---\n");

        for (TacheAbstraite t : taches) {
            if (!dependances.contains(t)) {
                sb.append(t.afficher(indent));
            }
        }

        return sb.toString();
    }
}
