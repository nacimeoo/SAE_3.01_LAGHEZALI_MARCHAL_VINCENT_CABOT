import application.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la classe Projet
 */
class TestProjet {

    private Projet projet;
    private Colonne colSource;
    private Colonne colDestination;
    private SousTache tache;

    /**
     * Initialisation d'un projet vide et de deux colonnes avant chaque test
     */
    @BeforeEach
    void setUp() {
        projet = new Projet(1, "Mon Projet", new Date());
        colSource = new Colonne("A faire");
        colDestination = new Colonne("En cours");
        tache = new SousTache(10, "Dev");
    }

    /**
     * Vérifie que le projet est bien instancié avec une liste de colonnes vide au départ
     */
    @Test
    void testConstructeur() {
        assertTrue(projet.getColonnes().isEmpty());
    }

    /**
     * Teste l'ajout de colonnes
     */
    @Test
    void testAjouterColonneOK() {
        projet.ajouterColonne(colSource);
        assertEquals(1, projet.getColonnes().size());
        assertEquals("A faire", projet.getColonnes().get(0).getNom());
    }

    /**
     * Teste la suppression d'une colonne
     */
    @Test
    void testSupprimerColonneOK() {
        projet.ajouterColonne(colSource);
        projet.ajouterColonne(colDestination);

        projet.supprimerColonne(0);

        assertEquals(1, projet.getColonnes().size());
        assertEquals("En cours", projet.getColonnes().get(0).getNom());
    }

    /**
     * Vérifie le déplacement d'une tâche
     */
    @Test
    void testDeplacerTacheOK() {
        projet.ajouterColonne(colSource);
        projet.ajouterColonne(colDestination);
        colSource.ajouterTache(tache);

        projet.deplacerTache(0, 1, tache);

        assertFalse(colSource.getTaches().contains(tache), "La tâche doit être retirée de la source");
        assertTrue(colDestination.getTaches().contains(tache), "La tâche doit être présente dans la destination");
    }

}