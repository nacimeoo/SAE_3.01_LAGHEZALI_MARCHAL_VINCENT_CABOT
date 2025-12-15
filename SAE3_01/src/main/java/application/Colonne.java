package application;

import java.util.ArrayList;
import java.util.List;

public class Colonne{
    protected int id;
    protected String nom;
    protected List<TacheAbstraite> taches;

    public Colonne(String nom) {
        this.nom = nom;
        this.taches = new ArrayList<>();
    }
    public void setId(int id) {
        this.id = id;
    }
    public void ajouterTache(TacheAbstraite ta){
        taches.add(ta);
    }

    public void supprimerTache(TacheAbstraite ta){
        taches.remove(ta);
    }

    public List<TacheAbstraite> getTaches() {
        return taches;
    }

    public String getNom() {
        return nom;
    }

    public String afficher(String indient) {

        ArrayList<TacheAbstraite> dependances = new ArrayList<>();

        for (TacheAbstraite t : taches) {
            if (t instanceof TacheMere) {
                dependances.addAll(((TacheMere) t).dependance);
            }
        }
        String s = "--- Colonne : " + nom + " ---";

        // Affichage uniquement des racines
        for (TacheAbstraite t : taches) {
            if (!dependances.contains(t)) {
                s += t.afficher(indient);
            }
        }
        return s;
    }
}
