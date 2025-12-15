import application.Projet;
import application.DAO.ProjetDAOImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

class TestProjetDAO {

    private ProjetDAOImpl projetDAO;

    @BeforeEach
    void setUp() {
        projetDAO = new ProjetDAOImpl();
    }

    @Test
    void testCycleDeVieProjet() throws Exception {
        Projet p = new Projet("Projet Test Integration", new Date());
        projetDAO.save(p);

        Assertions.assertTrue(p.getId() > 0, "L'ID du projet devrait être généré par la BDD");

        Projet recupere = projetDAO.getProjetById(p.getId());
        Assertions.assertNotNull(recupere, "Le projet devrait être trouvé en base");
        Assertions.assertEquals("Projet Test Integration", recupere.getNom());

        p.setNom("Projet Modifié");

        projetDAO.delete(p.getId());
        Projet apresSuppression = projetDAO.getProjetById(p.getId());
        Assertions.assertNull(apresSuppression, "Le projet ne devrait plus exister après suppression");
    }
}