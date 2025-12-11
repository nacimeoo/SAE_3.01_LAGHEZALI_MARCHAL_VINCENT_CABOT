package application;

import java.util.ArrayList;
import java.util.List;

public class Colonne {

    protected String nom;
    protected List<TacheAbstraite> taches;

    public Colonne(String nom) {
        this.nom = nom;
        this.taches = new ArrayList<>();
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

    public String afficher(){
        String s = "--- Colonne : " + nom + " ---";
        for(TacheAbstraite ta : taches){
            s += ta.afficher();
        }
        return s;
    }
}
