package application;

public class SousTache extends TacheAbstraite {

    public SousTache(String nom) {
        super(nom);
    }

    @Override
    public boolean ajouterDependance(TacheAbstraite t) {
        return false;
    }

    @Override
    public boolean supprimerDependance(TacheAbstraite t) {
        return false;
    }

    public boolean verifierDependance() {
        return false;
    }

    @Override
    public String afficher(String indent) {
        return "\n" + indent + "|- Sous-tâche : " + this.getNom();
    }
}
