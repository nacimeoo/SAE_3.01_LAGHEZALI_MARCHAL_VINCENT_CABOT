package application;

import application.DAO.ColonneDAOImpl;
import application.DAO.EtiquetteDAOImpl;
import application.DAO.ProjetDAOImpl;
import application.DAO.TacheDAOImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetService {

    private ProjetDAOImpl projetDAO = new ProjetDAOImpl();
    private ColonneDAOImpl colonneDAO = new ColonneDAOImpl();
    private TacheDAOImpl tacheDAO = new TacheDAOImpl();
    private EtiquetteDAOImpl etiquetteDAO = new EtiquetteDAOImpl();


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

    public void ajouterTache(Projet projet, Colonne colonne, TacheAbstraite tache) throws Exception {
        if (colonne == null || tache == null) return;
        tacheDAO.save(tache);
        colonneDAO.addTache(tache, colonne.getId());
        colonne.ajouterTache(tache);
        projet.notifierObservateurs();
    }

    public void supprimerTache(Projet projet, Colonne colonne, TacheAbstraite tache) throws Exception {
        if (colonne == null || tache == null) return;
        TacheAbstraite tacheCore = extraireBase(tache);
        tacheDAO.delete(tacheCore.getId());
        nettoyerTacheDeSaStructure(projet, tache);
        projet.notifierObservateurs();
    }

    public void deplacerTache(Projet projet, Colonne src, Colonne dest, TacheAbstraite tache) throws Exception {
        if (src == null || dest == null || tache == null) return;
        updateColonneEnBaseRecursif(tache, dest.getId());

        src.supprimerTache(tache);
        dest.ajouterTache(tache);
        projet.notifierObservateurs();
    }

    private void updateColonneEnBaseRecursif(TacheAbstraite t, int nouveauIdColonne) throws Exception {
        colonneDAO.deplacerTacheDAO(nouveauIdColonne, t.getId());
        TacheAbstraite core = extraireBase(t);

        if (core instanceof TacheMere) {
            TacheMere mere = (TacheMere) core;
            for (TacheAbstraite sousTache : mere.getSousTaches()) {
                updateColonneEnBaseRecursif(sousTache, nouveauIdColonne);
            }
        }
    }

    public boolean ajouterDependance(Projet projet, TacheMere mere, TacheAbstraite fille, Colonne colSource, Colonne colCible) throws Exception {
        if (mere == null || fille == null) return false;

        TacheAbstraite mereCore = extraireBase(mere);
        TacheAbstraite filleCore = extraireBase(fille);

        tacheDAO.updateType(mereCore.getId(), 0);

        int typePourFille = 1;
        if (filleCore instanceof TacheMere) {
            TacheMere filleMere = (TacheMere) filleCore;
            if (!filleMere.getSousTaches().isEmpty()) {
                typePourFille = 0;
            }
        }
        tacheDAO.updateType(filleCore.getId(), typePourFille);
        tacheDAO.addDependanceDAO(filleCore.getId(), mereCore.getId());
        mere.ajouterDependance(fille);

        if (colSource != null) {
            colSource.getTaches().remove(fille);
        }
        if (colSource != null && colCible != null && colSource.getId() != colCible.getId()) {
            colonneDAO.deplacerTacheDAO(colCible.getId(), filleCore.getId());
        }

        projet.notifierObservateurs();
        return true;
    }

    public void changerEtat(Projet projet, TacheAbstraite tache, String etat) throws Exception {
        if (projet == null || tache == null) return;

        TacheAbstraite tacheCore = extraireBase(tache);
        tacheDAO.updateEtat(etat, tacheCore.getId());
        tacheCore.setEtat(etat);
        tache.setEtat(etat);

        projet.notifierObservateurs();
    }

    public void modifierTache(Projet projet, TacheAbstraite tacheModifiee) throws Exception {
        if (projet == null || tacheModifiee == null) return;

        TacheAbstraite tacheRacine = extraireBase(tacheModifiee);

        tacheDAO.update_detail(tacheRacine);
        etiquetteDAO.supprimerLiensEtiquettes(tacheRacine.getId());

        TacheAbstraite courant = tacheModifiee;
        while (courant instanceof TacheDecorateur) {
            if (courant instanceof Etiquette) {
                Etiquette et = (Etiquette) courant;

                if (et.getIdEtiquette() == 0) {
                    etiquetteDAO.save(et);
                } else {
                    etiquetteDAO.save(et);
                }
                etiquetteDAO.attachEtiquetteToTache(et.getIdEtiquette(), tacheRacine.getId());
            }
            courant = ((TacheDecorateur) courant).getTacheDecoree();
        }

        boolean remplace = false;
        for (Colonne col : projet.getColonnes()) {
            List<TacheAbstraite> taches = col.getTaches();
            for (int i = 0; i < taches.size(); i++) {
                TacheAbstraite t = taches.get(i);

                TacheAbstraite tRacineDansListe = extraireBase(t);
                if (tRacineDansListe.getId() == tacheRacine.getId()) {
                    taches.set(i, tacheModifiee);
                    remplace = true;
                    break;
                }

                if (remplacerDansSousTaches(t, tacheRacine.getId(), tacheModifiee)) {
                    remplace = true;
                    break;
                }
            }
            if (remplace) break;
        }
        projet.notifierObservateurs();
    }

    private boolean remplacerDansSousTaches(TacheAbstraite parent, int idCible, TacheAbstraite tacheModifiee) {
        TacheAbstraite core = extraireBase(parent);

        if (core instanceof TacheMere) {
            TacheMere mere = (TacheMere) core;
            List<TacheAbstraite> sousTaches = mere.getSousTaches();

            for (int i = 0; i < sousTaches.size(); i++) {
                TacheAbstraite st = sousTaches.get(i);
                TacheAbstraite stCore = extraireBase(st);

                if (stCore.getId() == idCible) {
                    sousTaches.set(i, tacheModifiee);
                    return true;
                }

                if (remplacerDansSousTaches(st, idCible, tacheModifiee)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void detacherSousTache(Projet projet, TacheAbstraite tache, Colonne col) throws Exception {
        if (projet == null || tache == null) return;

        TacheAbstraite tacheCore = extraireBase(tache);

        tacheDAO.detacherSousTache(tacheCore.getId(), col.getId());
        TacheMere parent = trouverParent(projet, tache);
        nettoyerTacheDeSaStructure(projet, tache);
        col.ajouterTache(tache);
        projet.notifierObservateurs();
    }

    private void nettoyerTacheDeSaStructure(Projet projet, TacheAbstraite tache) {
        for (Colonne c : projet.getColonnes()) {
            c.getTaches().removeIf(t -> t.getId() == tache.getId());
        }
        TacheMere parent = trouverParent(projet, tache);
        if (parent != null) {
            parent.getSousTaches().removeIf(t -> t.getId() == tache.getId());
        }
    }

    public TacheMere trouverParent(Projet projet, TacheAbstraite fille) {
        for (Colonne c : projet.getColonnes()) {
            for (TacheAbstraite t : c.getTaches()) {
                TacheMere parent = chercherParentRecursif(t, fille);
                if (parent != null) return parent;
            }
        }
        return null;
    }

    private TacheMere chercherParentRecursif(TacheAbstraite current, TacheAbstraite cible) {
        TacheAbstraite core = extraireBase(current);

        if (core instanceof TacheMere) {
            TacheMere mere = (TacheMere) core;
            if (mere.getSousTaches().stream().anyMatch(t -> t.getId() == cible.getId())) {
                return mere;
            }
            for (TacheAbstraite sous : mere.getSousTaches()) {
                TacheMere res = chercherParentRecursif(sous, cible);
                if (res != null) return res;
            }
        }
        return null;
    }

    public Projet chargerProjetComplet(int idProjet) {
        try {
            Projet p = projetDAO.getProjetById(idProjet);
            if (p == null) return null;
            List<Colonne> colonnes = projetDAO.getColonnesByProjetId(p.getId());
            for (Colonne col : colonnes) {
                List<TacheAbstraite> taches = tacheDAO.getTachesByColonneId(col.getId());

                for (TacheAbstraite t : taches) {
                    TacheAbstraite tacheAvecEtiquette = chargerEtiquettes(t);
                    col.ajouterTache(tacheAvecEtiquette);
                }
                p.getColonnes().add(col);
            }
            return p;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifierEtatTacheMere(TacheAbstraite tache) {
        if (tache instanceof TacheMere) {
            TacheMere mere = (TacheMere) tache;
            for (TacheAbstraite sousTache : mere.getSousTaches()) {
                if (!"Terminer".equalsIgnoreCase(sousTache.getEtat())) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }


    public LocalDate ajusterDateSelonRegles(
            Projet projet,
            TacheAbstraite tache,
            LocalDate dateDemandee
    ) {
        if (dateDemandee == null) {
            return tache.getDateDebut();
        }


        TacheMere mere = trouverParent(projet, tache);
        if (mere != null) {
            LocalDate dateMere = mere.getDateDebut();
            if (dateMere != null && dateDemandee.isAfter(dateMere)) {
                return dateMere;
            }
        }


        if (tache instanceof TacheMere) {
            TacheMere tm = (TacheMere) tache;

            LocalDate maxSousTaches = null;
            for (TacheAbstraite st : tm.getSousTaches()) {
                if (st.getDateDebut() != null) {
                    if (maxSousTaches == null || st.getDateDebut().isAfter(maxSousTaches)) {
                        maxSousTaches = st.getDateDebut();
                    }
                }
            }

            if (maxSousTaches != null && dateDemandee.isBefore(maxSousTaches)) {
                return maxSousTaches;
            }
        }

        return dateDemandee;
    }

    /**
     * Méthode corrigée : charge les étiquettes récursivement pour une tâche et ses enfants.
     */
    private TacheAbstraite chargerEtiquettes(TacheAbstraite t) {
        if (t == null) return null;

        TacheAbstraite base = extraireBase(t);

        if (base instanceof TacheMere) {
            TacheMere mere = (TacheMere) base;
            List<TacheAbstraite> sousTachesAvecEtiquettes = new ArrayList<>();

            for (TacheAbstraite sousTache : mere.getSousTaches()) {
                TacheAbstraite sousTacheAvecEtiquettes = chargerEtiquettes(sousTache);
                sousTachesAvecEtiquettes.add(sousTacheAvecEtiquettes);
            }

            mere.getSousTaches().clear();
            mere.getSousTaches().addAll(sousTachesAvecEtiquettes);
        }

        TacheAbstraite tacheAvecEtiquettes = t;
        try {
            List<Etiquette> etiquettes = etiquetteDAO.getEtiquettesByTacheId(base.getId());
            for (Etiquette eInfo : etiquettes) {
                Etiquette etDecorateur = new Etiquette(tacheAvecEtiquettes, eInfo.getLibelle(), eInfo.getCouleur());
                etDecorateur.setIdEtiquette(eInfo.getIdEtiquette());
                tacheAvecEtiquettes = etDecorateur;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tacheAvecEtiquettes;
    }

    /**
     * Méthode pour charger les archives (sorti de chargerEtiquettes).
     */
    public List<TacheAbstraite> chargerArchives(int idProjet) {
        List<TacheAbstraite> archives = new ArrayList<>();
        try {
            archives = tacheDAO.getTachesArchivees(idProjet);

            for (int i = 0; i < archives.size(); i++) {
                TacheAbstraite t = archives.get(i);
                List<Etiquette> etiquettes = etiquetteDAO.getEtiquettesByTacheId(t.getId());
                for (Etiquette eInfo : etiquettes) {
                    Etiquette etDecorateur = new Etiquette(t, eInfo.getLibelle(), eInfo.getCouleur());
                    etDecorateur.setIdEtiquette(eInfo.getIdEtiquette());
                    t = etDecorateur;
                }
                archives.set(i, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return archives;
    }

    public TacheAbstraite extraireBase(TacheAbstraite tache) {
        TacheAbstraite core = tache;
        while (core instanceof TacheDecorateur) {
            core = ((TacheDecorateur) core).getTacheDecoree();
        }
        return core;
    }

    public void ajusterDateTacheMereSelonSousTaches(TacheMere mere) throws Exception {
        if (mere == null) return;

        LocalDate nouvelleDateDebut = mere.getDateDebut();

        for (TacheAbstraite sous : mere.getSousTaches()) {
            if (sous.getDateDebut() != null && sous.getDureeEstimee() > 0) {
                LocalDate finSous = sous.getDateDebut().plusDays(sous.getDureeEstimee());
                if (nouvelleDateDebut == null || finSous.isAfter(nouvelleDateDebut)) {
                    nouvelleDateDebut = finSous;
                }
            }
        }

        if (nouvelleDateDebut != null) {
            mere.setDateDebut(nouvelleDateDebut);
            tacheDAO.update_detail(mere);
        }
    }

    public void ajusterDatesRecursifVersHaut(Projet projet, TacheAbstraite tache) throws Exception {
        if (tache == null) return;

        TacheMere mere = trouverParent(projet, tache);

        if (mere != null) {
            ajusterDateTacheMereSelonSousTaches(mere);

            try {
                tacheDAO.update_detail(mere);

                ajusterDatesRecursifVersHaut(projet, mere);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}