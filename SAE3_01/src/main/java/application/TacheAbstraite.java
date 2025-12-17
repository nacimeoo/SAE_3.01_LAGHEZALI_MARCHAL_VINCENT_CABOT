package application;

import java.util.ArrayList;
import java.util.Date;

public abstract class TacheAbstraite{
    protected int id;
    protected String nom;
    protected String description;
    protected int priorite;
    protected Date dateDebut;
    protected Date dateFin;
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

    public abstract boolean verifierDependance();

    public abstract boolean ajouterDependance(TacheAbstraite t);

    public abstract boolean supprimerDependance(TacheAbstraite t);

    public abstract String afficher(String indient);

    public String afficherDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================\n");
        sb.append("DÉTAILS DE LA TÂCHE\n");
        sb.append("=======================================\n");
        sb.append("ID          : ").append(this.getId()).append("\n");
        sb.append("Titre       : ").append(this.getNom()).append("\n");
        sb.append("État        : ").append(this.getEtat()).append("\n");
        sb.append("Description : ").append(
                (this.description != null && !this.description.isEmpty()) ? this.description : "Aucune description"
        ).append("\n");

        return sb.toString();
    }


}
