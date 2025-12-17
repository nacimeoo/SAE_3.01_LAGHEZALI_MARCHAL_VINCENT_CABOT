package application;

import application.DAO.ColonneDAOImpl;
import application.DAO.ProjetDAOImpl;
import application.DAO.TacheDAOImpl;
import java.util.Date;

public class ProjetService {

    private ProjetDAOImpl projetDAO = new ProjetDAOImpl();
    private ColonneDAOImpl colonneDAO = new ColonneDAOImpl();
    private TacheDAOImpl tacheDAO = new TacheDAOImpl();

    public Projet creerProjet(String nom, Date dateCreation) throws Exception {
        Projet projet = new Projet(nom, dateCreation);
        projetDAO.save(projet);
        return projet;

    }

    public void ajouterColonne(Projet projet, Colonne colonne) throws Exception {
        if (projet == null || colonne == null) return;


        colonneDAO.save(colonne);


        projetDAO.addColonne(colonne, projet.getId());


        projet.getColonnes().add(colonne);


        projet.notifierObservateurs();
    }

    public void supprimerColonne(Projet projet, Colonne colonne) throws Exception {
        if (projet == null || colonne == null) return;

        colonneDAO.delete(colonne.getId());

        projet.getColonnes().remove(colonne);

        projet.notifierObservateurs();
    }

    public void ajouterTache(Colonne colonne, TacheAbstraite tache) throws Exception {
        if (colonne == null || tache == null) return;


        tacheDAO.save(tache);


        colonneDAO.addTache(tache, colonne.getId());


        colonne.ajouterTache(tache);
    }

    public void supprimerTache(Colonne colonne, TacheAbstraite tache) throws Exception {
        if (colonne == null || tache == null) return;


        tacheDAO.delete(tache.getId());


        colonne.supprimerTache(tache);
    }

    public void deplacerTache(Colonne src, Colonne dest, TacheAbstraite tache) throws Exception {
        if (src == null || dest == null || tache == null) return;


        colonneDAO.deplacerTacheDAO( dest.getId(), tache.getId());


        src.supprimerTache(tache);
        dest.ajouterTache(tache);
    }

    public boolean ajouterDependance(Projet projet,TacheMere mere, TacheAbstraite fille) throws Exception {
        if (mere == null || fille == null) return false;

        tacheDAO.addDependanceDAO(fille.getId(), mere.getId());

        mere.ajouterDependance(fille);

        boolean succes = mere.ajouterDependance(fille);

        return succes;
    }

    public void changerEtat(Projet projet, TacheAbstraite tache, String etat) throws Exception {
        if (projet == null || tache == null) return;

        tacheDAO.updateEtat(etat, tache.getId());

        tache.setEtat(etat);
    }
}
