package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheDAOImpl implements ITacheDAO {

    @Override
    public TacheAbstraite getTacheById(int id) throws Exception {
        String sql = "SELECT * FROM tache WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int type = rs.getInt("type");
                    TacheAbstraite tache;
                    if (type == 0) {
                        tache = new TacheMere(rs.getString("titre"));
                        tache.setId(rs.getInt("id"));
                        tache.setDescription(rs.getString("description"));
                        tache.setPriorite(rs.getInt("priorite"));
                        tache.setEtat(rs.getString("etat"));
                        chargerSousTaches((TacheMere) tache);
                    } else {
                        tache = new SousTache(rs.getString("titre"));
                        tache.setId(rs.getInt("id"));
                        tache.setDescription(rs.getString("description"));
                        tache.setPriorite(rs.getInt("priorite"));
                        tache.setEtat(rs.getString("etat"));
                    }
                    return tache;
                }
            }
        }
        return null;
    }

    public void chargerSousTaches(TacheMere mere) throws Exception {
        String sql = "SELECT id_sous_tache FROM dependance WHERE id_tache_mere = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mere.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TacheAbstraite enfant = getTacheById(rs.getInt("id_sous_tache"));
                    if (enfant != null) {
                        mere.ajouterDependance(enfant);
                    }
                }
            }
        }
    }

    @Override
    public List<TacheAbstraite> getAllTaches() throws Exception {
        String sql = "SELECT id FROM Tache";
        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                taches.add(getTacheById(rs.getInt("id")));
            }
            return taches;
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération de toutes les tâches", e);
        }
    }

    @Override
    public List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception {
        String sql = "SELECT t.id FROM Tache t " +
                "INNER JOIN colonne2tache c2t ON c2t.id_tache = t.id " +
                "WHERE c2t.id_colonne = ? " +
                "AND t.id NOT IN (SELECT id_sous_tache FROM dependance)";

        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, colonneId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(getTacheById(rs.getInt("id")));
                }
            }
            return taches;
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des tâches racines pour la colonne: " + colonneId, e);
        }
    }

    public void save(TacheAbstraite tache) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            boolean exist = false;
            String sqlSelect = "SELECT COUNT(*) FROM Tache WHERE id = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, tache.getId());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        exist = rs.getInt(1) > 0;
                    }
                }
            }

            if (exist) {
                String sqlUpdate = "UPDATE Tache SET titre = ? WHERE id = ?";
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, tache.getNom());
                    psUpdate.setInt(2, tache.getId());
                    psUpdate.executeUpdate();
                }
            } else {
                String sqlInsert = "INSERT INTO Tache (titre, type, etat) VALUES (?, ?, ?)";
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, tache.getNom());
                    psInsert.setInt(2, (tache instanceof TacheMere) ? 0 : 1);
                    psInsert.setString(3, tache.getEtat());
                    psInsert.executeUpdate();
                    try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            tache.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la sauvegarde (Save/Update)", e);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM Tache WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression de la tâche: " + id, e);
        }
    }

    public void updateEtat(String etat, int id) throws Exception {
        String sql = "UPDATE Tache SET etat = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etat);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void addDependanceDAO(int idF, int idM) {
        String sql = "INSERT INTO dependance (id_tache_mere, id_sous_tache) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idM);
            ps.setInt(2, idF);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update_detail(TacheAbstraite tache) throws Exception {
        String sql = "UPDATE Tache SET titre = ?, description = ?, priorite = ?, etat = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tache.getNom());
            stmt.setString(2, tache.getDescription());
            stmt.setInt(3, tache.getPriorite());
            stmt.setString(4, tache.getEtat());
            stmt.setInt(5, tache.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Erreur lors de la mise à jour des détails: " + tache.getId(), e);
        }
    }
}