package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//0=TacheMere
//1=SousTache

public class TacheDAOImpl implements ITacheDAO {


    private TacheAbstraite construireTache(ResultSet rs) throws SQLException {
        TacheAbstraite t;
        if (rs.getInt("type") == 0) {
            t = new TacheMere(rs.getString("titre"));
        } else {
            t = new SousTache(rs.getString("titre"));
        }

        t.setId(rs.getInt("id"));
        t.setDescription(rs.getString("description"));
        t.setPriorite(rs.getInt("priorite"));
        t.setEtat(rs.getString("etat"));

        Date sqlDate = rs.getDate("DateDebut");
        if (sqlDate != null) {
            t.setDateDebut(sqlDate.toLocalDate());
        }

        try {
            t.setDureeEstimee(rs.getInt("duree"));
        } catch (SQLException e) {
        }

        return t;
    }

    @Override
    public TacheAbstraite getTacheById(int id) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            return getTacheByIdInternal(id, con);
        }
    }

    private TacheAbstraite getTacheByIdInternal(int id, Connection con) throws Exception {
        String sql = "SELECT * FROM tache WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TacheAbstraite t = construireTache(rs);
                    if (t instanceof TacheMere) {
                        chargerSousTachesInternal((TacheMere) t, con);
                    }
                    return t;
                }
            }
        }
        return null;
    }

    private void chargerSousTachesInternal(TacheMere mere, Connection con) throws Exception {
        String sql = "SELECT id_sous_tache FROM dependance WHERE id_tache_mere = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mere.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TacheAbstraite enfant = getTacheByIdInternal(rs.getInt("id_sous_tache"), con);
                    if (enfant != null) {
                        mere.ajouterDependance(enfant);
                    }
                }
            }
        }
    }

    public void chargerSousTaches(TacheMere mere) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            chargerSousTachesInternal(mere, con);
        }
    }

    @Override
    public List<TacheAbstraite> getAllTaches() throws Exception {
        String sql = "SELECT * FROM tache WHERE etat <> 'Archivée'";
        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TacheAbstraite t = construireTache(rs);
                if (t instanceof TacheMere) {
                    // Utilisation de la méthode interne pour charger l'arbre complet
                    chargerSousTachesInternal((TacheMere) t, con);
                }
                taches.add(t);
            }
        }
        return taches;
    }

    @Override
    public List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception {
        String sql = """
            SELECT t.*
            FROM tache t
            INNER JOIN colonne2tache c2t ON c2t.id_tache = t.id
            WHERE c2t.id_colonne = ?
            AND t.id NOT IN (SELECT id_sous_tache FROM dependance)
            AND t.etat <> 'Archivée'
            """;

        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, colonneId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TacheAbstraite t = construireTache(rs);
                    if (t instanceof TacheMere) {
                        chargerSousTachesInternal((TacheMere) t, con);
                    }
                    taches.add(t);
                }
            }
        }
        return taches;
    }

    @Override
    public List<TacheAbstraite> getTachesArchivees(int idProjet) throws Exception {
        String sql = """
            SELECT DISTINCT t.* FROM tache t
            JOIN colonne2tache c2t ON t.id = c2t.id_tache
            JOIN colonne c ON c2t.id_colonne = c.id
            JOIN projet2colonne p2c ON c.id = p2c.id_colonne
            WHERE p2c.id_projet = ? AND t.etat = 'Archivée'
        """;

        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProjet);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TacheAbstraite t = construireTache(rs);
                    if (t instanceof TacheMere) {
                        chargerSousTachesInternal((TacheMere) t, con);
                    }
                    taches.add(t);
                }
            }
        }
        return taches;
    }

    public void save(TacheAbstraite tache) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            boolean exist = false;
            String sqlSelect = "SELECT COUNT(*) FROM tache WHERE id = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, tache.getId());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) exist = rs.getInt(1) > 0;
                }
            }

            if (exist) {
                String sqlUpdate = """
                        UPDATE tache
                        SET titre = ?, type = ?, description = ?, priorite = ?, etat = ?, DateDebut = ?, duree = ?
                        WHERE id = ?
                        """;
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, tache.getNom());
                    psUpdate.setInt(2, (tache instanceof TacheMere) ? 0 : 1);
                    psUpdate.setString(3, tache.getDescription());
                    psUpdate.setInt(4, tache.getPriorite());
                    psUpdate.setString(5, tache.getEtat());
                    if (tache.getDateDebut() != null) {
                        psUpdate.setDate(6, Date.valueOf(tache.getDateDebut()));
                    } else {
                        psUpdate.setNull(6, Types.DATE);
                    }
                    psUpdate.setInt(7, tache.getDureeEstimee());
                    psUpdate.setInt(8, tache.getId());
                    psUpdate.executeUpdate();
                }
            } else {
                String sqlInsert = """
                        INSERT INTO tache (titre, type, description, priorite, etat, DateDebut, duree)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, tache.getNom());
                    psInsert.setInt(2, (tache instanceof TacheMere) ? 0 : 1);
                    psInsert.setString(3, tache.getDescription());
                    psInsert.setInt(4, tache.getPriorite());
                    psInsert.setString(5, tache.getEtat());
                    if (tache.getDateDebut() != null) {
                        psInsert.setDate(6, Date.valueOf(tache.getDateDebut()));
                    } else {
                        psInsert.setNull(6, Types.DATE);
                    }
                    psInsert.setInt(7, tache.getDureeEstimee());
                    psInsert.executeUpdate();
                    try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            tache.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM tache WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateEtat(String etat, int id) throws Exception {
        String sql = "UPDATE tache SET etat = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etat);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void addDependanceDAO(int idF, int idM) {
        String sqlInsertDep = "INSERT INTO dependance (id_tache_mere, id_sous_tache) VALUES (?, ?)";
        String sqlUpdateChild = "UPDATE tache SET type = 1 WHERE id = ?";
        String sqlUpdateParent = "UPDATE tache SET type = 0 WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psDep = con.prepareStatement(sqlInsertDep);
                 PreparedStatement psChild = con.prepareStatement(sqlUpdateChild);
                 PreparedStatement psParent = con.prepareStatement(sqlUpdateParent)) {

                psDep.setInt(1, idM);
                psDep.setInt(2, idF);
                psDep.executeUpdate();

                psChild.setInt(1, idF);
                psChild.executeUpdate();

                psParent.setInt(1, idM);
                psParent.executeUpdate();

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update_detail(TacheAbstraite tache) throws Exception {
        String sql = """
                UPDATE tache
                SET titre = ?, description = ?, priorite = ?, etat = ?, DateDebut = ?, duree = ?
                WHERE id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tache.getNom());
            stmt.setString(2, tache.getDescription());
            stmt.setInt(3, tache.getPriorite());
            stmt.setString(4, tache.getEtat());
            if (tache.getDateDebut() != null) {
                stmt.setDate(5, Date.valueOf(tache.getDateDebut()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setInt(6, tache.getDureeEstimee());
            stmt.setInt(7, tache.getId());
            stmt.executeUpdate();
        }
    }

    public void detacherSousTache(int idTache, int idColonne) throws Exception {
        String sqlDeleteDep = "DELETE FROM dependance WHERE id_sous_tache = ?";
        String sqlDeleteCol = "DELETE FROM colonne2tache WHERE id_tache = ?";
        String sqlInsertCol = "INSERT INTO colonne2tache (id_colonne, id_tache) VALUES (?, ?)";
        String sqlUpdateType = "UPDATE tache SET type = 0 WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psDep = con.prepareStatement(sqlDeleteDep);
                 PreparedStatement psDelCol = con.prepareStatement(sqlDeleteCol);
                 PreparedStatement psInsCol = con.prepareStatement(sqlInsertCol);
                 PreparedStatement psUpdateType = con.prepareStatement(sqlUpdateType)) {

                psDep.setInt(1, idTache);
                psDep.executeUpdate();

                psDelCol.setInt(1, idTache);
                psDelCol.executeUpdate();

                psInsCol.setInt(1, idColonne);
                psInsCol.setInt(2, idTache);
                psInsCol.executeUpdate();

                psUpdateType.setInt(1, idTache);
                psUpdateType.executeUpdate();

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}