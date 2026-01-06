package application.DAO;

import application.Etiquette;
import application.TacheAbstraite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EtiquetteDAOImpl implements IEtiquetteDAO {

    @Override
    public Etiquette getEtiquetteById(int id, int id_tache) throws Exception {
        try (Connection connection = DBConnection.getConnection();) {
            String sql = "SELECT * FROM Etiquette WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    TacheDAOImpl tacheDAO = new TacheDAOImpl();
                    Etiquette etiquette = new Etiquette(tacheDAO.getTacheById(id_tache), rs.getString("nom"), rs.getString("couleur"));
                    etiquette.setId(rs.getInt("id"));
                    return etiquette;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connection", e);
        }
    }

    @Override
    public List<Etiquette> getAllEtiquettes() throws Exception {
        List<Etiquette> etiquettes = new ArrayList<>();
        String sql = "SELECT e.*, te.id_tache FROM Etiquette e " +
                "JOIN tache2etiquette te ON e.id = te.id_etiquette";

        TacheDAOImpl tacheDAO = new TacheDAOImpl();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idTache = rs.getInt("id_tache");
                TacheAbstraite tache = tacheDAO.getTacheById(idTache);

                // Vérification indispensable avant de décorer
                if (tache != null) {
                    Etiquette etiquette = new Etiquette(tache, rs.getString("nom"), rs.getString("couleur"));
                    etiquette.setId(rs.getInt("id"));
                    etiquettes.add(etiquette);
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur récupération", e);
        }
        return etiquettes;
    }

    public void save(Etiquette etiquette) throws Exception {
        String sqlExist = "SELECT COUNT(*) FROM Etiquette WHERE id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement stmtExist = connection.prepareStatement(sqlExist)) {
                stmtExist.setInt(1, etiquette.getId());
                try (ResultSet rs = stmtExist.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }

            if (exists) {
                String sqlUpdate = "UPDATE Etiquette SET nom = ?, couleur = ? WHERE id = ?";
                try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setString(1, etiquette.getLibelle());
                    stmtUpdate.setString(2, etiquette.getCouleur());
                    stmtUpdate.setInt(3, etiquette.getId());
                    stmtUpdate.executeUpdate();
                }
            } else {
                String sqlInsert = "INSERT INTO Etiquette (nom, couleur) VALUES (?, ?)";
                try (PreparedStatement stmtInsert = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmtInsert.setString(1, etiquette.getLibelle());
                    stmtInsert.setString(2, etiquette.getCouleur());
                    stmtInsert.executeUpdate();
                    try (ResultSet generatedKeys = stmtInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            etiquette.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la sauvegarde de l'étiquette", e);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Etiquette WHERE id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (Exception e){
                throw new Exception("Erreur lors de l'exécution de la suppression de l'étiquette avec l'ID: " + id, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connection: " + e);
        }
    }

    @Override
    public List<Etiquette> getEtiquettesByTacheId(int tacheId) throws Exception {
        String sql = "SELECT e.id, e.nom, e.couleur FROM Etiquette e " +
                     "JOIN tache2etiquette te ON e.id = te.id_etiquette " +
                     "WHERE te.id_tache = ?";
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, tacheId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Etiquette> etiquettes = new ArrayList<>();
                while (rs.next()) {
                    TacheDAOImpl tacheDAO = new TacheDAOImpl();
                    Etiquette etiquette = new Etiquette(tacheDAO.getTacheById(tacheId), rs.getString("nom"), rs.getString("couleur"));
                    etiquette.setId(rs.getInt("id"));
                    etiquettes.add(etiquette);
                }
                return etiquettes;
            } catch (Exception e) {
                throw new Exception("Erreur lors de la récupération des étiquettes pour la tâche avec l'ID: " + tacheId, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }
    }

    public void attachEtiquetteToTache(int etiquetteId, int tacheId) throws Exception {
        String sql = "INSERT INTO tache2etiquette (id_tache, id_etiquette) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);) {
                stmt.setInt(1, tacheId);
                stmt.setInt(2, etiquetteId);
                stmt.executeUpdate();
            } catch (Exception e){
                throw new Exception("Erreur lors de l'exécution de l'attachement de l'étiquette avec l'ID: " + etiquetteId + " à la tâche avec l'ID: " + tacheId, e);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la connexion", e);
        }
    }

    public void supprimerLiensEtiquettes(int idTache) throws Exception {
        String sql = "DELETE FROM tache2etiquette WHERE id_tache = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTache);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression des liens étiquettes pour la tâche " + idTache, e);
        }
    }
}
