package application.DAO;

import application.Colonne;

import java.util.List;

public interface IColonneDAO {
    Colonne getColonneById(int id) throws Exception;
    List<Colonne> getAllColonnes() throws Exception;
    void save(Colonne colonne) throws Exception;
    void delete(int id) throws Exception;
}
