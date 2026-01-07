package application;

import application.DAO.ITacheDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Projet implements Sujet {

    private int id;
    private String nom;
    private Date dateCreation;
    private List<Colonne> colonnes = new ArrayList<>();
    private List<Observateur> observateurs = new ArrayList<>();

    public Projet(String nom, Date dateCreation) {
        this.nom = nom;
        this.dateCreation = dateCreation;
    }
    @Override
    public void enregistrerObservateur(Observateur o) {
        if (o != null) observateurs.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) o.actualiser(this);
    }

    public Colonne trouverColonneParId(int idColonne) {
        for (Colonne c : colonnes) {
            if (c.getId() == idColonne) return c;
        }
        return null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Colonne> getColonnes() {
        return colonnes;
    }

    public List<TacheAbstraite> getTaches() {
        List<TacheAbstraite> toutes = new ArrayList<>();
        for (Colonne c : colonnes) {
            toutes.addAll(c.getTaches());
        }
        return toutes;
    }

    public List<TacheAbstraite> getAllTaches() {
        List<TacheAbstraite> toutes = new ArrayList<>();

        for (Colonne colonne : colonnes) {
            for (TacheAbstraite tache : colonne.getTaches()) {
                ajouterTacheAvecSousTaches(tache, toutes);
            }
        }

        return toutes;
    }

    private void ajouterTacheAvecSousTaches(TacheAbstraite tache, List<TacheAbstraite> liste) {
        if (!liste.contains(tache)) {
            liste.add(tache);
        }

        if (tache instanceof TacheMere) {
            for (TacheAbstraite sousTache : ((TacheMere) tache).getSousTaches()) {
                ajouterTacheAvecSousTaches(sousTache, liste);
            }
        }
    }



    public TacheAbstraite getTacheById(String id) {
        int idTache = Integer.parseInt(id);
        for (TacheAbstraite t : getAllTaches()) {
            if (t.getId() == idTache) {
                return t;
            }
        }
        return null;
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

    public int getId() { return id; }

    public String getNom() { return nom; }


    public Date getDateCreation() { return dateCreation; }


    public void setNom(String nom) {
        this.nom = nom;
    }
}


