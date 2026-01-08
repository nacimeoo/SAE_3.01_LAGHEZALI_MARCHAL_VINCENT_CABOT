package application.DAO;

import application.Colonne;
import application.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAOImpl implements IProjetDAO {
    @Override
    public Projet getProjetById(int id) throws Exception {
        String sql = "SELECT * FROM projet WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Projet projet = new Projet(rs.getString("nom"), rs.getDate("dateCreation"));
                    projet.setId(rs.getInt("id"));
                    return projet;
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'exécution de la requête pour le projet avec l'ID: " + id, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion à la base de données", e);
        }
    }

    @Override
    public List<Projet> getAllProjets() throws Exception {
        String sql = "SELECT * FROM projet";
        List<Projet> projets = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Projet projet = new Projet(rs.getString("nom"), rs.getDate("dateCreation"));
                    projet.setId(rs.getInt("id"));
                    projets.add(projet);
                }
                return projets;
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'exécution de la requête pour tous les projets", e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }
    }

    @Override


    public void save(Projet projet) throws Exception {
        String sql = "INSERT INTO projet (nom, dateCreation) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, projet.getNom());
            stmt.setDate(2, new java.sql.Date(projet.getDateCreation().getTime()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenere = generatedKeys.getInt(1);
                        projet.setId(idGenere);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la sauvegarde du projet ou de la connexion à la base de données.", e);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {

            String sqlGetColonnes = "SELECT id_colonne FROM projet2colonne WHERE id_projet = ?";
            List<Integer> colonneIds = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(sqlGetColonnes)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        colonneIds.add(rs.getInt("id_colonne"));
                    }
                    ColonneDAOImpl colonneDAO = new ColonneDAOImpl();
                    for (Integer colonneID : colonneIds) {
                        colonneDAO.delete(colonneID);
                    }
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de la récupération des colonnes du projet " + id, e);
            }

            String sqlDeleteLinks = "DELETE FROM projet2colonne WHERE id_projet = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteLinks)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            String sqlDeleteProjet = "DELETE FROM projet WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteProjet)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            throw new Exception("Erreur globale lors de la suppression du projet", e);
        }
    }

    @Override
    public List<Colonne> getColonnesByProjetId(int projetId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.id, c.titre FROM colonne c " +
                    "INNER JOIN projet2colonne cp ON c.id = cp.id_colonne " +
                    "WHERE cp.id_projet = ?";
            List<Colonne> colonnes = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, projetId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Colonne colonne = new Colonne(rs.getString("titre"));
                        colonne.setId(rs.getInt("id"));
                        colonnes.add(colonne);
                    }
                    return colonnes;
                } catch (Exception e) {
                    throw new Exception("Erreur lors de l'exécution de la requête pour les colonnes du projet avec l'ID: " + projetId, e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }
    }

    public void addColonne(Colonne colonne, int idProj) throws SQLException, ClassNotFoundException {
        try(Connection conn = DBConnection.getConnection()){
            String sql = "INSERT INTO projet2colonne (id_projet, id_colonne) VALUES (?, ?)";
            try ( PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idProj);
                stmt.setInt(2, colonne.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
