import application.Colonne;
import application.DAO.ColonneDAOImpl;
import application.DAO.DBConnection;
import application.DAO.IColonneDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestColonneDAO {

    private IColonneDAO colonneDAO;
    private Colonne colonneTest;

    @BeforeEach
    void setUp() throws Exception {
        colonneDAO = new ColonneDAOImpl();
        colonneTest = new Colonne("Colonne Test 1");
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM colonnes WHERE nom LIKE 'Colonne Test%'");
        } catch (Exception e) {
        }
    }

    @Test
    void testSaveAndGetById() throws Exception {
        colonneDAO.save(colonneTest);

        assertTrue(colonneTest.getId() > 0);

        Colonne colonneRecuperee = colonneDAO.getColonneById(colonneTest.getId());

        assertNotNull(colonneRecuperee);
        assertEquals("Colonne Test 1", colonneRecuperee.getNom());
    }

    @Test
    void testGetAllColonnes() throws Exception {
        colonneDAO.save(new Colonne("Colonne Test 2"));
        colonneDAO.save(new Colonne("Colonne Test 3"));

        List<Colonne> colonnes = colonneDAO.getAllColonnes();

        assertTrue(colonnes.size() >= 2);

        assertTrue(colonnes.stream().anyMatch(c -> c.getNom().equals("Colonne Test 2")));
        assertTrue(colonnes.stream().anyMatch(c -> c.getNom().equals("Colonne Test 3")));
    }

    @Test
    void testDelete() throws Exception {
        colonneDAO.save(colonneTest);
        int idASupprimer = colonneTest.getId();

        colonneDAO.delete(idASupprimer);

        Colonne colonneSupprimee = colonneDAO.getColonneById(idASupprimer);

        assertNull(colonneSupprimee);
    }
}