package application.DAO;

import application.Colonne;
import application.TacheAbstraite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColonneDAOImpl implements IColonneDAO {

    @Override
    public Colonne getColonneById(int id) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {;
            String query = "SELECT * FROM colonnes WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Colonne colonne = new Colonne(resultSet.getString("nom"));;
                        colonne.setId(resultSet.getInt("id"));
                        return colonne;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    throw new Exception("Erreur lors de la récupération de la colonne avec l'ID: " + id, e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion à la base de données", e);
        }
    }

    @Override
    public List<Colonne> getAllColonnes() throws Exception {
        try (Connection connection = DBConnection.getConnection()) {;
            String query = "SELECT * FROM colonnes";
            List<Colonne> colonnes = new ArrayList<>();
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        Colonne colonne = new Colonne(resultSet.getString("nom"));
                        colonne.setId(resultSet.getInt("id"));
                        colonnes.add(colonne);
                    }
                    return colonnes;
                } catch (Exception e) {
                    throw new Exception("Erreur lors de la récupération de toutes les colonnes", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion à la base de données", e);
        }
    }

    @Override
    public void save(Colonne colonne) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {;
            String query = "INSERT INTO colonnes (nom) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, colonne.getNom());
                statement.executeUpdate();
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'insertion de la colonne", e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion à la base de données", e);
        }

    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {;
            String sql = "DELETE FROM colonnes WHERE id = ?";
            String sql2 = "DELETE FROM projet2colonne WHERE id_colonne = ?";
            String sql3 = "SELECT id_tache FROM colonne2tache WHERE id_colonne = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new Exception("Erreur lors de la suppression de la colonne pour l'ID: " + id, e);
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql2)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new Exception("Erreur lors de la suppression des associations colonne-projet pour l'ID: " + id, e);
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql3)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Integer> tacheIds = new ArrayList<>();
                    while (rs.next()) {
                        tacheIds.add(rs.getInt("id_tache"));
                    }
                    TacheDAOImpl tacheDAO = new TacheDAOImpl();
                    for (Integer tacheId : tacheIds) {
                        tacheDAO.delete(tacheId);
                    }
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de la récupération et suppression des tâches associées pour la colonne avec l'ID: " + id, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void addTache(TacheAbstraite t, int idColonne) throws SQLException, ClassNotFoundException {
        try(Connection conn = DBConnection.getConnection()){
            String sql = "INSERT INTO colonne2tache (id_colonne, id_tache) VALUES (?, ?)";
            try ( PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idColonne);
                stmt.setInt(2, t.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deplacerTacheDAO(int idColonneDest, int id) throws SQLException, ClassNotFoundException {
        try(Connection conn = DBConnection.getConnection()){
            String sql = "UPDATE FROM colonne2tache SET id_colonne = ? WHERE id_tache = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idColonneDest);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            }
        }
    }
}
