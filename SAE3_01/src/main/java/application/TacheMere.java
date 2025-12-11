package application;

import java.util.ArrayList;

public class TacheMere extends TacheAbstraite {

    ArrayList<TacheAbstraite> dependance = new ArrayList<>();

    public TacheMere(int id, String nom) {
        super(id, nom);
    }

    @Override
    public boolean ajouterDependance(TacheAbstraite t) {
        dependance.add(t);
        return true;
    }

    @Override
    public boolean supprimerDependance(TacheAbstraite t) {
        dependance.remove(t);
        return true;
    }

    @Override
    public String afficher(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n")
                .append(indent)
                .append("|- Tâche Mère : ")
                .append(this.getNom());

        // Ajouter les dépendances en arbre
        for (TacheAbstraite t : dependance) {
            sb.append(t.afficher(indent + "   "));
        }

        return sb.toString();
    }
}
