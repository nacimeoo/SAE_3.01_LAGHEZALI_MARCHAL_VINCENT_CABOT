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
    private ProjetService projetService;
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
        projetService = new ProjetService();
        projet.setId(1);
        colSource = new Colonne("A faire");
        colDestination = new Colonne("En cours");
        tache = new SousTache("Dev");

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
    void testAjouterColonneOK() throws Exception {
        projetService.ajouterColonne(projet,colSource);
        assertEquals(1, projet.getColonnes().size());
        assertEquals("A faire", projet.getColonnes().get(0).getNom());
    }

    /**
     * Teste la suppression d'une colonne
     */
    @Test
    void testSupprimerColonneOK() throws Exception {
        projetService.ajouterColonne(projet,colSource);
        projetService.ajouterColonne(projet, colDestination);

        projetService.supprimerColonne(projet, projet.getColonnes().get(0));

        assertEquals(1, projet.getColonnes().size());
        assertEquals("En cours", projet.getColonnes().get(0).getNom());
    }

    /**
     * Vérifie le déplacement d'une tâche
     */
    @Test
    void testDeplacerTacheOK() throws Exception {
        projetService.ajouterColonne(projet,colSource);
        projetService.ajouterColonne(projet, colDestination);
        colSource.ajouterTache(tache);

        projetService.deplacerTache(projet,projet.getColonnes().get(0), projet.getColonnes().get(1), tache);

        assertFalse(colSource.getTaches().contains(tache), "La tâche doit être retirée de la source");
        assertTrue(colDestination.getTaches().contains(tache), "La tâche doit être présente dans la destination");
    }

    @Test
    void testAjouterTacheDansColonne() throws Exception {
        projetService.ajouterColonne(projet, colSource);

        projetService.ajouterTache(projet,projet.getColonnes().get(0),tache);

        assertTrue(colSource.getTaches().contains(tache));

    }

    /**
     * Teste la suppression d'une tâche via le projet et la notification.
     */
    @Test
    void testSupprimerTacheDeColonne() throws Exception {
        projetService.ajouterColonne(projet, colSource);
        projetService.ajouterTache(projet, projet.trouverColonneParId(0),tache);


        projetService.supprimerTache(projet, projet.trouverColonneParId(0),tache);

        assertTrue(colSource.getTaches().isEmpty());
    }

    /**
     * Vérifie le changement d'état et la notification MVC
     */
    @Test
    void testChangerEtatTache() throws Exception {
        projetService.ajouterColonne(projet, colSource);
        projetService.ajouterTache(projet , projet.trouverColonneParId(0), tache);


        projetService.changerEtat(projet,tache,"Terminer");

        assertEquals("Terminer", tache.getEtat());
    }

    /**
     * Teste l'ajout de dépendance entre une tâche mère et une sous-tâche.
     */
    @Test
    void testAjouterDependanceTache() throws Exception {
        TacheMere mere = new TacheMere( "Big Task");
        SousTache fille = new SousTache( "Small Task");

        projetService.ajouterColonne(projet, colSource);
        projetService.ajouterTache(projet, projet.getColonnes().get(0),mere);
        projetService.ajouterTache(projet, projet.getColonnes().get(0), fille);


        boolean succes = projetService.ajouterDependance(projet, mere, fille);

        assertTrue(succes);
    }

    /**
     * Vérifie qu'on ne peut pas ajouter une colonne nulle.
     */
    @Test
    void testAjouterColonneNull() throws Exception {
        projetService.ajouterColonne(projet, null);

        assertTrue(projet.getColonnes().isEmpty());
    }



}