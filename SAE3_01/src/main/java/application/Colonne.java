package application;

import java.util.List;

public class Colonne {

    protected String nom;
    protected List<TacheAbstraite> taches;

    public Colonne(String nom) {
        this.nom = nom;
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
        return "--- Colonne : " + nom + " ---\n";
    }
}
