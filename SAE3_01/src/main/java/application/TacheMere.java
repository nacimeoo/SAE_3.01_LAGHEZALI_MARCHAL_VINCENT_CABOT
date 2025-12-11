package application;

import java.util.ArrayList;

public class TacheMere extends TacheAbstraite {
    ArrayList<SousTache> dependance = new ArrayList<>();
    public TacheMere(int id, String nom) {
        super(id, nom);
    }

    @Override
    public boolean ajouterDependance(TacheAbstraite t) {
        dependance.add((SousTache) t);
        return true;
    }

    @Override
    public boolean supprimerDependance(TacheAbstraite t) {
        dependance.remove((SousTache) t);
        return true;
    }

    @Override
    public String afficher() {
        return "- Tache Mere : " + getNom();
    }
}
