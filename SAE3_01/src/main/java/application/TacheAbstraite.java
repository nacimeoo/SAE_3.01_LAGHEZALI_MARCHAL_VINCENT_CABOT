package application;

import java.util.ArrayList;
import java.util.Date;

public abstract class TacheAbstraite implements Sujet{
    protected int id;
    protected String nom;
    protected String description;
    protected int priorite;
    protected Date dateDebut;
    protected Date dateFin;
    protected int dureeEstimee;
    protected String etat;
    protected ArrayList<Observateur> observateurs;


    public TacheAbstraite(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.etat = "A faire";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public int getDureeEstimee() {
        return dureeEstimee;
    }

    public void setDureeEstimee(int dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public void enregistrerObservateur(Observateur o) {
        this.observateurs.add(o);

    }

    @Override
    public void supprimerObservateur(Observateur o) {
        int i = this.observateurs.indexOf(o);
        if (i >= 0) {
            this.observateurs.remove(i);
        }
    }

    @Override
    public void notifierObservateurs() {
        for (int i = 0; i < this.observateurs.size(); i++) {
            Observateur observer = this.observateurs.get(i);
            observer.actualiser(this);
        }
    }

    public abstract boolean verifierDependance();

    public abstract boolean ajouterDependance(TacheAbstraite t);

    public abstract boolean supprimerDependance(TacheAbstraite t);

    public abstract String afficher(String indient);


}
