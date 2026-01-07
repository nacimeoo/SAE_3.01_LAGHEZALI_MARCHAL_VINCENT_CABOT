package application;

import java.time.LocalDate;

public abstract class TacheDecorateur extends TacheAbstraite{

    protected TacheAbstraite tacheDecoree;

    public TacheDecorateur(TacheAbstraite tacheDecoree) {
        super(tacheDecoree != null ? tacheDecoree.getNom() : "");
        this.tacheDecoree = tacheDecoree;
    }

    @Override
    public boolean verifierDependance() {
        return tacheDecoree.verifierDependance();
    }

    @Override
    public boolean ajouterDependance(TacheAbstraite t) {
        return tacheDecoree.ajouterDependance(t);
    }

    @Override
    public boolean supprimerDependance(TacheAbstraite t) {
        return tacheDecoree.supprimerDependance(t);
    }

    @Override
    public String afficher(String indient) {
        return tacheDecoree.afficher(indient);
    }


    @Override
    public int getId() { return tacheDecoree.getId(); }
    @Override
    public void setId(int id) { tacheDecoree.setId(id); }

    @Override
    public LocalDate getDate() { return tacheDecoree.getDate(); }

    @Override
    public String getNom() { return tacheDecoree.getNom(); }
    @Override
    public void setNom(String nom) { tacheDecoree.setNom(nom); }

    @Override
    public String getDescription() { return tacheDecoree.getDescription(); }
    @Override
    public void setDescription(String description) { tacheDecoree.setDescription(description); }

    @Override
    public int getPriorite() { return tacheDecoree.getPriorite(); }
    @Override
    public void setPriorite(int priorite) { tacheDecoree.setPriorite(priorite); }

    @Override
    public LocalDate getDateDebut() { return tacheDecoree.getDateDebut(); }
    @Override
    public void setDateDebut(LocalDate dateDebut) { tacheDecoree.setDateDebut(dateDebut); }

    @Override
    public int getDureeEstimee() { return tacheDecoree.getDureeEstimee(); }
    @Override
    public void setDureeEstimee(int dureeEstimee) { tacheDecoree.setDureeEstimee(dureeEstimee); }

    @Override
    public String getEtat() { return tacheDecoree.getEtat(); }
    @Override
    public void setEtat(String etat) { tacheDecoree.setEtat(etat); }
    public TacheAbstraite getTacheDecoree() { return tacheDecoree; }

    @Override
    public String afficherDetails() { return tacheDecoree.afficherDetails();}

}

