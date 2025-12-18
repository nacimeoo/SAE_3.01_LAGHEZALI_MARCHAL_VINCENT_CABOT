package application.DAO;

import application.Etiquette;

import java.util.List;

public interface IEtiquetteDAO {

    Etiquette getEtiquetteById(int id, int id_tache) throws Exception;
    List<Etiquette> getAllEtiquettes() throws Exception;

    void save(Etiquette etiquette) throws Exception;

    void delete(int id) throws Exception;
    List<Etiquette> getEtiquettesByTacheId(int tacheId) throws Exception;
    void attachEtiquetteToTache(int etiquetteId, int tacheId) throws Exception;

}
