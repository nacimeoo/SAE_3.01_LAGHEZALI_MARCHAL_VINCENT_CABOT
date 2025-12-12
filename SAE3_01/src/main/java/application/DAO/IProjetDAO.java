package application.DAO;

import application.Colonne;
import application.Projet;

import java.util.List;

public interface IProjetDAO {
    Projet getProjetById(int id) throws Exception;
    List<Projet> getAllProjets() throws Exception;
    void save(Projet projet) throws Exception;
    void delete(int id) throws Exception;

    List<Colonne> getColonnesByProjetId(int projetId) throws Exception;
}
