import application.DAO.*;
import application.Etiquette;
import application.TacheAbstraite;
import application.TacheMere;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
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
        tache = new TacheMere("Urgent");
        etiquetteTest = new Etiquette(tache,"Urgent", "#FF0000");
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        tacheDAO.save(tache);
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
//            stmt.executeUpdate("DELETE FROM etiquette WHERE nom LIKE 'Test%' OR nom = 'Urgent'");
//            stmt.executeUpdate("DELETE FROM Tache WHERE nom = 'Urgent'");
//            stmt.executeUpdate("DELETE FROM tache2etiquette WHERE id_tache NOT IN (SELECT id FROM Tache)");
        } catch (Exception e) {
        }
    }

    @Test
    void testSaveAndGetById() throws Exception {
        etiquetteDAO.save(etiquetteTest);

        assertTrue(etiquetteTest.getId() > 0);

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(etiquetteTest.getId(), tache.getId());

        assertNotNull(recuperee);
        assertEquals("Urgent", recuperee.getLibelle());
        assertEquals("#FF0000", recuperee.getCouleur());
    }

    @Test
    void testGetAllEtiquettes() throws Exception {
        etiquetteDAO.save(new Etiquette(tache, "Test1", "#000000"));
        etiquetteDAO.save(new Etiquette(tache, "Test2", "#FFFFFF"));

        List<Etiquette> etiquettes = etiquetteDAO.getAllEtiquettes();


        assertTrue(etiquettes.size() >= 2);
        assertTrue(etiquettes.stream().anyMatch(e -> e.getLibelle().equals("Test1")));
        assertTrue(etiquettes.stream().anyMatch(e -> e.getLibelle().equals("Test2")));
    }

    @Test
    void testUpdate() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        int id = etiquetteTest.getId();

        etiquetteTest.setLibelle("TestUpdate");
        etiquetteTest.setCouleur("#123456");
        etiquetteDAO.save(etiquetteTest);

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(id, tache.getId());
        assertEquals("TestUpdate", recuperee.getLibelle());
        assertEquals("#123456", recuperee.getCouleur());
    }

    @Test
    void testDelete() throws Exception {
        etiquetteDAO.save(etiquetteTest);
        int id = etiquetteTest.getId();

        etiquetteDAO.delete(id);

        Etiquette recuperee = etiquetteDAO.getEtiquetteById(id, tache.getId());
        assertNull(recuperee);
    }
}