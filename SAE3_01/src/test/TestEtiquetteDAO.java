import application.DAO.*;
import application.Etiquette;
import application.TacheAbstraite;
import application.TacheMere;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestEtiquetteDAO {

    private IEtiquetteDAO etiquetteDAO;
    private Etiquette etiquetteTest;
    private TacheAbstraite tache;
    private ITacheDAO tacheDAO;

    @BeforeEach
    void setUp() throws Exception {
        tacheDAO = new TacheDAOImpl();
        etiquetteDAO = new EtiquetteDAOImpl();

        tache = new TacheMere("TestTacheUrgent");
        tacheDAO.save(tache);

        etiquetteTest = new Etiquette(tache, "TestEtiquette", "#FF0000");
    }

    @AfterEach
    void tearDown() {
        String sqlCleanLinks = "DELETE FROM tache2etiquette WHERE id_etiquette IN (SELECT id FROM etiquette WHERE nom LIKE 'Test%')";
        String sqlCleanEtiquettes = "DELETE FROM etiquette WHERE nom LIKE 'Test%'";
        String sqlCleanTaches = "DELETE FROM tache WHERE titre LIKE 'Test%'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sqlCleanLinks);
            stmt.executeUpdate(sqlCleanEtiquettes);
            stmt.executeUpdate(sqlCleanTaches);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndGetById() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        assertTrue(etiquetteTest.getIdEtiquette() > 0, "L'ID de l'étiquette devrait être généré");

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(etiquetteTest.getIdEtiquette(), tache.getId());

        assertNotNull(recuperee, "L'étiquette récupérée ne doit pas être null");
        assertEquals("TestEtiquette", recuperee.getLibelle());
        assertEquals("#FF0000", recuperee.getCouleur());
    }

    @Test
    void testGetAllEtiquettes() throws Exception {
        Etiquette e1 = new Etiquette(tache, "Test1", "#000000");
        Etiquette e2 = new Etiquette(tache, "Test2", "#FFFFFF");

        etiquetteDAO.save(e1);
        etiquetteDAO.save(e2);

        etiquetteDAO.attachEtiquetteToTache(e1.getIdEtiquette(), tache.getId());
        etiquetteDAO.attachEtiquetteToTache(e2.getIdEtiquette(), tache.getId());

        List<Etiquette> etiquettes = etiquetteDAO.getAllEtiquettes();

        assertFalse(etiquettes.isEmpty());

        boolean trouve1 = etiquettes.stream().anyMatch(e -> e.getLibelle().equals("Test1"));
        boolean trouve2 = etiquettes.stream().anyMatch(e -> e.getLibelle().equals("Test2"));

        assertTrue(trouve1, "L'étiquette Test1 devrait être dans la liste");
        assertTrue(trouve2, "L'étiquette Test2 devrait être dans la liste");
    }

    @Test
    void testUpdate() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        int id = etiquetteTest.getIdEtiquette();

        etiquetteTest.setLibelle("TestUpdate");
        etiquetteTest.setCouleur("#123456");

        etiquetteDAO.save(etiquetteTest);

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(id, tache.getId());
        assertEquals("TestUpdate", recuperee.getLibelle());
        assertEquals("#123456", recuperee.getCouleur());
        assertEquals(id, recuperee.getIdEtiquette());
    }

    @Test
    void testDelete() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        int id = etiquetteTest.getIdEtiquette();

        etiquetteDAO.delete(id);

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(id, tache.getId());
        assertNull(recuperee, "L'étiquette ne devrait plus exister après suppression");
    }

    @Test
    void testGetEtiquettesByTacheId() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        etiquetteDAO.attachEtiquetteToTache(etiquetteTest.getIdEtiquette(), tache.getId());

        List<Etiquette> results = etiquetteDAO.getEtiquettesByTacheId(tache.getId());

        assertFalse(results.isEmpty());
        assertEquals("TestEtiquette", results.get(0).getLibelle());
    }
}