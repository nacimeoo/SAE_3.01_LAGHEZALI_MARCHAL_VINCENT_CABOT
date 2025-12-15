package application.DAO;

import application.Colonne;
import application.Projet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAOImpl implements IProjetDAO {
    @Override
    public Projet getProjetById(int id) throws Exception {
        String sql = "SELECT * FROM projets WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Projet(rs.getInt("id"), rs.getString("nom"), rs.getDate("date"));
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'exécution de la requête pour le projet avec l'ID: " + id, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion");
        }
    }

    @Override
    public List<Projet> getAllProjets() throws Exception {
        String sql = "SELECT * FROM projets";
        List<Projet> projets = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Projet projet = new Projet(rs.getInt("id"), rs.getString("nom"), rs.getDate("date"));
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
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO projets (nom, date) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, projet.getNom());
                stmt.setDate(2, new java.sql.Date(projet.getDateCreation().getTime()));
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'exécution de la requête de sauvegarde du projet", e);
            }

        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }

    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM projets WHERE id = ?";
            String sql2 = "SELECT id_colonne FROM colonne2projet WHERE id_projet = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'exécution de la requête de suppression du projet avec l'ID: " + id, e);
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Integer> colonneIds = new ArrayList<>();
                    while (rs.next()) {
                        colonneIds.add(rs.getInt("id_colonne"));
                    }
                    ColonneDAOImpl colonneDAO = new ColonneDAOImpl();
                    for (Integer colonneID : colonneIds) {
                        colonneDAO.delete(colonneID);
                    }
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de la suppression des associations projet-colonne pour l'ID: " + id, e);
            }

        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }
    }

    @Override
    public List<Colonne> getColonnesByProjetId(int projetId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.id, c.nom FROM colonnes c " +
                    "INNER JOIN colonne2projet cp ON c.id = cp.id_colonne " +
                    "WHERE cp.id_projet = ?";
            List<Colonne> colonnes = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, projetId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Colonne colonne = new Colonne(rs.getString("nom"));
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
}
