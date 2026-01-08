package application;

public class Etiquette extends TacheDecorateur{

    private String libelle;
    private int id;
    private String couleur;

    public Etiquette(TacheAbstraite tacheDecoree, String libelle, String couleur) {
        super(tacheDecoree);
        this.libelle = libelle;
        this.couleur = couleur;
    }


    public String getLibelle() {
        return libelle;
    }

    public int getIdEtiquette() {
        return id;
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

    public String getCouleur() {
        return couleur;
    }

    public void setIdEtiquette(int id) {
        this.id = id;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
