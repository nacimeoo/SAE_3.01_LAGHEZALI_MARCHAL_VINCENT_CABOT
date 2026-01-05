package application.DAO;

import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        return t;
    }

    @Override
    public TacheAbstraite getTacheById(int id) throws Exception {
        String sql = "SELECT * FROM Tache WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TacheAbstraite t = construireTache(rs);
                    if (t instanceof TacheMere) {
                        chargerSousTaches((TacheMere) t);
                    }
                    return t;
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
        String sql = "SELECT * FROM Tache";
        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                taches.add(construireTache(rs));
            }
        }
        return taches;
    }

    @Override
    public List<TacheAbstraite> getTachesByColonneId(int colonneId) throws Exception {
        String sql = """
                SELECT t.*
                FROM Tache t
                INNER JOIN colonne2tache c2t ON c2t.id_tache = t.id
                WHERE c2t.id_colonne = ?
                """;
        List<TacheAbstraite> taches = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, colonneId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    taches.add(construireTache(rs));
                }
            }
        }
        return taches;
    }

    public void save(TacheAbstraite tache) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            boolean exist = false;
            String sqlSelect = "SELECT COUNT(*) FROM Tache WHERE id = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, tache.getId());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) exist = rs.getInt(1) > 0;
                }
            }

            if (exist) {
                String sqlUpdate = """
                        UPDATE Tache
                        SET titre = ?, type = ?, description = ?, priorite = ?, etat = ?, DateDebut = ?
                        WHERE id = ?
                        """;
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, tache.getNom());
                    psUpdate.setInt(2, (tache instanceof TacheMere) ? 0 : 1);
                    psUpdate.setString(3, tache.getDescription());
                    psUpdate.setInt(4, tache.getPriorite());
                    psUpdate.setString(5, tache.getEtat());
                    if (tache.getDate() != null) {
                        psUpdate.setDate(6, Date.valueOf(tache.getDate()));
                    } else {
                        psUpdate.setNull(6, Types.DATE);
                    }
                    psUpdate.setInt(7, tache.getId());
                    psUpdate.executeUpdate();
                }
            } else {
                String sqlInsert = """
                        INSERT INTO Tache (titre, type, description, priorite, etat, DateDebut)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, tache.getNom());
                    psInsert.setInt(2, (tache instanceof TacheMere) ? 0 : 1);
                    psInsert.setString(3, tache.getDescription());
                    psInsert.setInt(4, tache.getPriorite());
                    psInsert.setString(5, tache.getEtat());
                    if (tache.getDate() != null) {
                        psInsert.setDate(6, Date.valueOf(tache.getDate()));
                    } else {
                        psInsert.setNull(6, Types.DATE);
                    }
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
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DELETE FROM colonne2tache WHERE id_tache = " + id);
            stmt.executeUpdate("DELETE FROM tache2etiquette WHERE id_tache = " + id);
            stmt.executeUpdate("DELETE FROM Tache WHERE id = " + id);
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
        String sql = """
                UPDATE Tache
                SET titre = ?, description = ?, priorite = ?, etat = ?, DateDebut = ?
                WHERE id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tache.getNom());
            stmt.setString(2, tache.getDescription());
            stmt.setInt(3, tache.getPriorite());
            stmt.setString(4, tache.getEtat());
            if (tache.getDate() != null) {
                stmt.setDate(5, Date.valueOf(tache.getDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setInt(6, tache.getId());
            stmt.executeUpdate();
        }
    }
}
