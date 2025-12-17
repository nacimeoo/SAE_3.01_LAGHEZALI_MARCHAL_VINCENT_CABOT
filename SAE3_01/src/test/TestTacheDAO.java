
import application.DAO.DBConnection;
import application.DAO.ITacheDAO;
import application.DAO.TacheDAOImpl;
import application.SousTache;
import application.TacheAbstraite;
import application.TacheMere;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestTacheDAO {

    private ITacheDAO tacheDAO;
    private TacheMere tacheMere;
    private SousTache sousTache;

    @BeforeEach
    void setUp() {
        tacheDAO = new TacheDAOImpl();
        tacheMere = new TacheMere("Tache Mere Test");
        sousTache = new SousTache("Sous Tache Test");
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Tache WHERE nom LIKE '%Test'");
            stmt.executeUpdate("DELETE FROM colonne2tache WHERE id_tache NOT IN (SELECT id FROM Taches)");
        } catch (Exception e) {
        }
    }

    @Test
    void testSaveAndGetTacheMere() throws Exception {
        tacheDAO.save(tacheMere);

        assertTrue(tacheMere.getId() > 0);

        TacheAbstraite recuperee = tacheDAO.getTacheById(tacheMere.getId());

        assertNotNull(recuperee);
        assertTrue(recuperee instanceof TacheMere);
        assertEquals("Tache Mere Test", recuperee.getNom());
    }

    @Test
    void testSaveAndGetSousTache() throws Exception {
        tacheDAO.save(sousTache);

        assertTrue(sousTache.getId() > 0);

        TacheAbstraite recuperee = tacheDAO.getTacheById(sousTache.getId());

        assertNotNull(recuperee);
        assertTrue(recuperee instanceof SousTache);
        assertEquals("Sous Tache Test", recuperee.getNom());
    }

    @Test
    void testUpdateTache() throws Exception {
        tacheDAO.save(tacheMere);
        int idInitial = tacheMere.getId();

        tacheMere.setNom("Tache Mere Mise à Jour");
        tacheDAO.save(tacheMere);

        TacheAbstraite recuperee = tacheDAO.getTacheById(idInitial);

        assertEquals(idInitial, recuperee.getId());
        assertEquals("Tache Mere Mise à Jour", recuperee.getNom());
    }

    @Test
    void testDeleteTache() throws Exception {
        tacheDAO.save(tacheMere);
        int idASupprimer = tacheMere.getId();

        tacheDAO.delete(idASupprimer);

        TacheAbstraite recuperee = tacheDAO.getTacheById(idASupprimer);

        assertNull(recuperee);
    }
}