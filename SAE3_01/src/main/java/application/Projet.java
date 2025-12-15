package application;

import java.util.ArrayList;
import java.util.Date;

public class Projet implements Sujet {
    private int id;
    private String nom;
    private Date dateCreation;
    private ArrayList<Colonne> colonnes;
    private ArrayList<Observateur> observateurs = new ArrayList<>();

    public Projet(String nom, Date dateCreation) {
        this.nom = nom;
        this.dateCreation = dateCreation;
        this.colonnes = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void enregistrerObservateur(Observateur o) {
        observateurs.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) {
            o.actualiser(this);
        }
    }

    public void ajouterColonne(Colonne colonne){
        if (colonne != null){
            colonnes.add(colonne);
            notifierObservateurs();
        }
    }

    public void supprimerColonne(int idColonne){
        if (idColonne >= 0 && idColonne < colonnes.size()) {
            colonnes.remove(idColonne);
            notifierObservateurs();
        }
    }

    public void ajouterTacheDansColonne(TacheAbstraite t, int indexColonne) {
        if (indexColonne >= 0 && indexColonne < colonnes.size()) {
            colonnes.get(indexColonne).ajouterTache(t);
            notifierObservateurs();
        }
    }

    public void supprimerTacheDeColonne(TacheAbstraite t, int indexColonne) {
        if (indexColonne >= 0 && indexColonne < colonnes.size()) {
            colonnes.get(indexColonne).supprimerTache(t);
            notifierObservateurs();
        }
    }

    public void deplacerTache(int idColonneSource, int idColonneDestination, TacheAbstraite tache) {
        if (idColonneSource >= 0 && idColonneSource < colonnes.size() &&
                idColonneDestination >= 0 && idColonneDestination < colonnes.size()) {

            Colonne colSrc = colonnes.get(idColonneSource);
            Colonne colDest = colonnes.get(idColonneDestination);

            if (colSrc.getTaches().contains(tache)) {
                colSrc.supprimerTache(tache);
                colDest.ajouterTache(tache);
                notifierObservateurs();
            }
        }
    }

    public void changerEtatTache(TacheAbstraite t, String nouvelEtat) {
        t.setEtat(nouvelEtat);
        notifierObservateurs();
    }

    public boolean ajouterDependanceTache(TacheMere mere, TacheAbstraite fille) {
        boolean succes = mere.ajouterDependance(fille);
        if (succes) {
            notifierObservateurs();
        }
        return succes;
    }

    public ArrayList<Colonne> getColonnes() {
        return colonnes;
    }

    public ArrayList<TacheAbstraite> getTache() {
        ArrayList<TacheAbstraite> toutesLesTaches = new ArrayList<>();
        for (Colonne c : colonnes) {
            toutesLesTaches.addAll(c.getTaches());
        }
        return toutesLesTaches;
    }

    public void afficher(String indent) {
        System.out.println("========================================================");
        System.out.println(" PROJET #" + id + ": " + nom + " : " + dateCreation);
        System.out.println("========================================================");

        if (colonnes.isEmpty()) {
            System.out.println("(Aucune colonne)");
        } else {
            for (Colonne colonne : colonnes) {
                System.out.println(colonne.afficher(indent));
            }
        }
    }


    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}