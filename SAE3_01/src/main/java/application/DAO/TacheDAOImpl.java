package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.Connection;
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
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getInt("type") == 0) {
                    TacheMere t = new TacheMere(rs.getInt("id"), rs.getString("nom"));
                } else {
                    SousTache t = new SousTache(rs.getInt("id"), rs.getString("nom"));
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
                    TacheMere t = new TacheMere(rs.getInt("id"), rs.getString("nom"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getInt("id"), rs.getString("nom"));
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
                    TacheMere t = new TacheMere(rs.getInt("id"), rs.getString("nom"));
                    taches.add(t);
                } else {
                    SousTache t = new SousTache(rs.getInt("id"), rs.getString("nom"));
                    taches.add(t);
                }
            }
            return taches;
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des tâches pour la colonne avec l'ID: " + colonneId, e);
        }
    }

    @Override
    public void save(TacheAbstraite tache) throws Exception {
        boolean exist = false;
        String sql1 = "Select count(*) as count from Taches where id = " + tache.getId();
        Statement stmt1 = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt1.executeQuery(sql1);
        if (rs != null && rs.next()) {
            exist = true;
        }

        if (exist) {
            String sqlUpdate = "UPDATE Taches SET nom = ? WHERE id = ?";
            try (Connection con = DBConnection.getConnection();) {
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sqlUpdate);
            } catch (Exception e) {
                throw new Exception("Erreur lors de la mise à jour de la tâche avec l'ID: " + tache.getId(), e);
            }
        } else {
            String sqlInsert = "INSERT INTO Taches (id, nom, type) VALUES (?, ?, ?)";
            try (Connection con = DBConnection.getConnection();) {
                Statement stmt = con.createStatement();
                int type = (tache instanceof TacheMere) ? 0 : 1;
                stmt.executeUpdate(sqlInsert);
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'insertion de la nouvelle tâche", e);
            }
        }

    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Taches WHERE id = " + id;
        try (Connection con = DBConnection.getConnection();) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression de la tâche avec l'ID: " + id, e);
        }
    }
}
