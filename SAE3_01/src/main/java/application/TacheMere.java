package application;

import java.util.ArrayList;

public class TacheMere extends TacheAbstraite {

    ArrayList<TacheAbstraite> dependance = new ArrayList<>();

    public TacheMere(String nom) {
        super(nom);
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
    public boolean verifierDependance() {
        int cpt =  0;
        for (TacheAbstraite t : dependance) {
            if (!t.getEtat().equals("Terminer")){
                cpt++;
            }
        }
        return cpt < 0;
    }

    @Override
    public String afficher(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n")
                .append(indent)
                .append("|- Tâche Mère : ")
                .append(this.getNom());

        for (TacheAbstraite t : dependance) {
            sb.append(t.afficher(indent + "   "));
        }

        return sb.toString();
    }

    public ArrayList<TacheAbstraite> getSousTaches() {
        return this.dependance;
    }
}
