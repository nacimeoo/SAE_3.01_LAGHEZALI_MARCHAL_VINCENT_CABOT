package application.DAO;

import application.TacheAbstraite;

import java.util.List;

public interface ITacheDAO {
    TacheAbstraite getTacheById(int id) throws Exception;
    List<TacheAbstraite> getAllTaches() throws Exception;
    List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception;
    void save(TacheAbstraite tache) throws Exception;
    void delete(int id) throws Exception;
}
