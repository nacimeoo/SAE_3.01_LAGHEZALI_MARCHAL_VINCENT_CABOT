import application.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la classe Colonne
 */
class TestColonne {

    private Colonne colonne;
    private SousTache t;

    @BeforeEach
    void setUp() {
        colonne = new Colonne("A faire");
        t = new SousTache("Conception");
        t.setId(50);
    }

    /**
     * Vérifie l'ajout d'une tâche
     */
    @Test
    void testAjouterTache() {
        colonne.ajouterTache(t);
        assertEquals(1, colonne.getTaches().size());
    }

    /**
     * Vérifie la suppression d'une tâche
     */
    @Test
    void testSupprimerTache() {

        colonne.ajouterTache(t);
        assertEquals(1, colonne.getTaches().size());

        colonne.supprimerTache(t);
        assertTrue(colonne.getTaches().isEmpty());
    }

    /**
     * Teste affichage
     */
    @Test
    void testAfficher() {
        TacheMere mere = new TacheMere("Dev");
        mere.setId(1);
        SousTache fille = new SousTache("Analyse");
        mere.setId(2);

        mere.ajouterDependance(fille);

        colonne.ajouterTache(mere);
        colonne.ajouterTache(fille);

        String resultatAffichage = colonne.afficher(" ");

        // On vérifie que le titre de la colonne et les noms des taches sont présent
        assertTrue(resultatAffichage.contains("--- Colonne : A faire ---"));
        assertTrue(resultatAffichage.contains("Dev"));
        assertTrue(resultatAffichage.contains("Analyse"));
    }
}