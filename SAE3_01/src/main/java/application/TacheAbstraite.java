package application;

import application.DAO.TacheDAOImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public abstract class TacheAbstraite{
    protected int id;
    protected String nom;
    protected String description;
    protected int priorite;
    protected LocalDate dateDebut;
    protected int dureeEstimee;
    protected String etat;


    public TacheAbstraite(String nom) {
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

    public LocalDate getDate(){
        return dateDebut;
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

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
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

    public abstract boolean verifierDependance();

    public abstract boolean ajouterDependance(TacheAbstraite t);

    public abstract boolean supprimerDependance(TacheAbstraite t);

    public abstract String afficher(String indient);

    public String afficherDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================\n");
        sb.append("DÉTAILS DE LA TÂCHE\n");
        sb.append("=======================================\n");
        sb.append("ID : ").append(this.getId()).append("\n");
        sb.append("Titre : ").append(this.getNom()).append("\n");
        sb.append("État : ").append(this.getEtat()).append("\n");
        sb.append("duree Estimee : ").append(this.getDureeEstimee()).append(" jours").append("\n");

        return sb.toString();
    }


}
