package application;

public class Etiquette extends TacheDecorateur{

    private String libelle;
    private int id;

    public Etiquette(TacheAbstraite tacheDecoree, String libelle) {
        super(tacheDecoree);
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String afficher(String indent) {
        return super.afficher(indent) + " [Etiquette: " + this.libelle + "]";
    }

    @Override
    public String afficherDetails() {
        return super.afficherDetails() + "Étiquette   : " + this.getLibelle() + "\n";
    }
}
