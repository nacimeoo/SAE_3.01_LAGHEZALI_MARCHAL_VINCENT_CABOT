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
    private SousTache  tache;
    private boolean observerNotifier;

    /**
     * Initialisation d'un projet vide et de deux colonnes avant chaque test
     */
    @BeforeEach
    void setUp() {
        projet = new Projet("Mon Projet", new Date());
        projet.setId(1);
        colSource = new Colonne("A faire");
        colDestination = new Colonne("En cours");
        tache = new SousTache(10, "Dev");

        observerNotifier = false;
        projet.enregistrerObservateur(s -> observerNotifier = true);
        tache = new SousTache("Dev");
        tache.setId(10);
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

    @Test
    void testAjouterTacheDansColonne() {
        projet.ajouterColonne(colSource);
        observerNotifier = false;

        projet.ajouterTacheDansColonne(tache, 0);

        assertTrue(colSource.getTaches().contains(tache));
        assertTrue(observerNotifier);
    }

    /**
     * Teste la suppression d'une tâche via le projet et la notification.
     */
    @Test
    void testSupprimerTacheDeColonne() {
        projet.ajouterColonne(colSource);
        projet.ajouterTacheDansColonne(tache, 0);

        observerNotifier = false;

        projet.supprimerTacheDeColonne(tache, 0);

        assertTrue(colSource.getTaches().isEmpty());
        assertTrue(observerNotifier);
    }

    /**
     * Vérifie le changement d'état et la notification MVC
     */
    @Test
    void testChangerEtatTache() {
        projet.ajouterColonne(colSource);
        projet.ajouterTacheDansColonne(tache, 0);

        observerNotifier = false;

        projet.changerEtatTache(tache, "Terminer");

        assertEquals("Terminer", tache.getEtat());
        assertTrue(observerNotifier);
    }

    /**
     * Teste l'ajout de dépendance entre une tâche mère et une sous-tâche.
     */
    @Test
    void testAjouterDependanceTache() {
        TacheMere mere = new TacheMere(100, "Big Task");
        SousTache fille = new SousTache(101, "Small Task");

        projet.ajouterColonne(colSource);
        projet.ajouterTacheDansColonne(mere, 0);
        projet.ajouterTacheDansColonne(fille, 0);


        boolean succes = projet.ajouterDependanceTache(mere, fille);

        assertTrue(succes);
    }

    /**
     * Vérifie qu'on ne peut pas ajouter une colonne nulle.
     */
    @Test
    void testAjouterColonneNull() {
        projet.ajouterColonne(null);

        assertTrue(projet.getColonnes().isEmpty());
    }



}