package application;

public class Description extends TacheDecorateur{

    private String libelle;

    public Description(TacheAbstraite tacheDecoree, String libelle) {
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
        return super.afficher(indent);
    }

    @Override
    public String afficherDetails() {
        return super.afficherDetails() + "Description (Ajout) : " + this.libelle + "\n";
    }
}
