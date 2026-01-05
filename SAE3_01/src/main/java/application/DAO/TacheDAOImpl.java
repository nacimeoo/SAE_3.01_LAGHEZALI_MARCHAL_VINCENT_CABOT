package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 0 pour TacheMere et 1 pour SousTache

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
                        chargerSousTaches((TacheMere) tache);
                    } else {
                        tache = new SousTache(rs.getString("titre"));
                        tache.setId(rs.getInt("id"));
                    }
                    return tache;
                }
            }
        }
        return null;
    }

    public void chargerSousTaches(TacheMere mere) throws Exception {
        String sql = "SELECT id_sous_tache FROM dependance WHERE id_tache_mere = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM Tache";
        List<TacheAbstraite> taches = new ArrayList<TacheAbstraite>();
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getString("titre"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getString("titre"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                }
            }
            return taches;
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération de toutes les tâches", e);
        }
    }

    @Override
    public List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception {
        String sql = "SELECT * FROM Tache " +
                "inner join colonne2tache on colonne2tache.id_tache = tache.id "
                + "WHERE colonne2tache.id_colonne = " + colonneId;
        List<TacheAbstraite> taches = new ArrayList<TacheAbstraite>();
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getString("titre"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getString("titre"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                }
            }
            return taches;
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des tâches pour la colonne avec l'ID: " + colonneId, e);
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
                } catch (Exception e) {
                    throw new Exception("Erreur lors de la mise à jour de la tâche avec l'ID: " + tache.getId(), e);
                }
            } else {
                String sqlInsert = "INSERT INTO Tache (titre, type) VALUES (?, ?)";

                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    int type = (tache instanceof TacheMere) ? 0 : 1;

                    psInsert.setString(1, tache.getNom());
                    psInsert.setInt(2, type);

                    int affectedRows = psInsert.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                tache.setId(generatedKeys.getInt(1));
                            }
                        } catch (Exception e) {
                            throw new Exception("Erreur lors de la récupération de l'ID généré pour la nouvelle tâche", e);
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("Erreur lors de l'insertion de la nouvelle tâche", e);
                }
            }

        } catch (Exception e) {
            throw new Exception("Une erreur générale est survenue lors de l'opération Save.", e);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Tache WHERE id = " + id;
        String sql2 = "DELETE FROM colonne2tache WHERE id_tache = " + id;
        String sql3 = "DELETE FROM tache2etiquette WHERE id_tache = " + id;
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression de la tâche avec l'ID: " + id, e);
        }
    }

    public void updateEtat(String etat, int id) throws Exception {
        String sql = "UPDATE Tache SET etat = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, etat);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void addDependanceDAO(int idF, int idM) {
        String sql = "INSERT INTO dependance (id_tache_mere, id_sous_tache) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idM);
            ps.setInt(2, idF);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update_detail(TacheAbstraite tache) throws Exception {
        String sql = "UPDATE Tache SET titre = ?, description = ?, priorite = ?, etat = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, tache.getNom());
            stmt.setString(2, tache.getDescription());
            stmt.setInt(3, tache.getPriorite());
            stmt.setString(4, tache.getEtat());
            stmt.setInt(5, tache.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Erreur lors de la mise à jour de la tâche avec l'ID: " + tache.getId(), e);
        }
    }
}
