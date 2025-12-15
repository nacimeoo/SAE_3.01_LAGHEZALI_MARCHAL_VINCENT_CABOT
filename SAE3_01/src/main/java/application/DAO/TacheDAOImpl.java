package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// 0 pour TacheMere et 1 pour SousTache

public class TacheDAOImpl implements ITacheDAO {

    @Override
    public TacheAbstraite getTacheById(int id) throws Exception {
        String sql = "SELECT * FROM Taches WHERE id = ?";
        try (Connection con = DBConnection.getConnection();) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getString("nom"));
                    t.setId(rs.getInt("id"));
                    return t;
                } else {
                    SousTache t = new SousTache(rs.getString("nom"));
                    t.setId(rs.getInt("id"));
                    return t;
                }

            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération de la tâche avec l'ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<TacheAbstraite> getAllTaches() throws Exception {
        String sql = "SELECT * FROM Taches";
        List<TacheAbstraite> taches = new ArrayList<TacheAbstraite>();
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getString("nom"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getString("nom"));
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
        String sql = "SELECT * FROM Taches" +
                "inner join colonne2tache on colonne2tache.id_tache = taches.id "
                + "WHERE colonne2tache.id_colonne = " + colonneId;
        List<TacheAbstraite> taches = new ArrayList<TacheAbstraite>();
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getString("nom"));
                    t.setId(rs.getInt("id"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getString("nom"));
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

            String sqlSelect = "SELECT COUNT(*) FROM Taches WHERE id = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, tache.getId());

                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        exist = rs.getInt(1) > 0;
                    }
                }
            }

            if (exist) {
                String sqlUpdate = "UPDATE Taches SET nom = ? WHERE id = ?";
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, tache.getNom());
                    psUpdate.setInt(2, tache.getId());
                    psUpdate.executeUpdate();
                } catch (Exception e) {
                    throw new Exception("Erreur lors de la mise à jour de la tâche avec l'ID: " + tache.getId(), e);
                }
            } else {String sqlInsert = "INSERT INTO Taches (nom, type) VALUES (?, ?)";
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                    int type = (tache instanceof TacheMere) ? 0 : 1;

                    psInsert.setString(1, tache.getNom());
                    psInsert.setInt(2, type);

                    psInsert.executeUpdate();
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
        String sql = "DELETE FROM Taches WHERE id = " + id;
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
}
