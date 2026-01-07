package application.DAO;

import application.TacheAbstraite;
import application.TacheMere;

import java.util.List;

public interface ITacheDAO {
    TacheAbstraite getTacheById(int id) throws Exception;
    List<TacheAbstraite> getAllTaches() throws Exception;
    List<TacheAbstraite> getAllTachesArchivee() throws Exception;
    List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception;
    void save(TacheAbstraite tache) throws Exception;
    void delete(int id) throws Exception;
    void update_detail(TacheAbstraite tache) throws Exception;
    void addDependanceDAO(int idTacheMere, int idSousTache) throws Exception;
    void chargerSousTaches(TacheMere mere) throws Exception;
    void detacherSousTache(int idTache, int idColonne) throws Exception;

    List<TacheAbstraite> getTachesArchivees(int idProjet) throws Exception;
}
