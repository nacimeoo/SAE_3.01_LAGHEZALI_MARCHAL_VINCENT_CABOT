package application.DAO;

import java.util.List;

public interface IEtiquetteDAO {
    // A compléter avec la classe Etiquette
    void delete(int id) throws Exception;



    List<String> getEtiquettesByTacheId(int tacheId) throws Exception;

}
