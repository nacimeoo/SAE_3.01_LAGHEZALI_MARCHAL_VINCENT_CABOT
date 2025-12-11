package application;

import java.util.ArrayList;

public class TacheMere extends TacheAbstraite {
    ArrayList<SousTache> dependance = new ArrayList<>();
    public TacheMere(int id, String nom) {
        super(id, nom);
    }

    @Override
    public boolean ajouterDependance(TacheAbstraite t) {
        if (t instanceof SousTache) {;
            dependance.add((SousTache) t);
            return true;
        }
        return false;
    }

    @Override
    public boolean supprimerDependance(TacheAbstraite t) {
        if (t instanceof SousTache) {;
            dependance.remove((SousTache) t);
            return true;
        }
        return false;
    }

    @Override
    public String afficher() {
        return "- Tache Mere : " + getNom();
    }
}
