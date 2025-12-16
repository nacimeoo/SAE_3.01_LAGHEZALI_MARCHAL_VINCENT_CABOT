import application.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la hiérarchie des tâches (Composite Pattern)
 */
class TacheTest {

    /**
     * Vérifie qu'une SousTache agit comme une "feuille"
     */
    @Test
    void testSousTache() {
        SousTache st = new SousTache(10, "dev");
        TacheAbstraite autre = new SousTache(11, "");

        assertFalse(st.ajouterDependance(autre), "Une sous-tâche ne doit pas accepter de dépendance");
        assertFalse(st.supprimerDependance(autre));
        assertTrue(st.afficher("").contains("Sous-tâche : dev"));
    }

    /**
     * Vérifie l'ajout d'une dépendance
     */
    @Test
    void testTacheMereAjoutDependanceOK() {
        TacheMere mere = new TacheMere(20, "Grosse Tâche");
        SousTache fille = new SousTache(21, "Partie 1");

        assertTrue(mere.ajouterDependance(fille));

        //On vérfie que la classe mère possede bien la classe fille
        String affichage = mere.afficher("");
        assertTrue(affichage.contains("Partie 1"));
    }

    /**
     * Vérifie la suppression d'une dépendance
     */
    @Test
    void testTacheMereSuppressionDependanceOK() {
        TacheMere mere = new TacheMere(20, "Grosse Tâche");
        SousTache fille = new SousTache(21, "Partie 1");

        mere.ajouterDependance(fille);
        assertTrue(mere.supprimerDependance(fille));

        String affichage = mere.afficher("");
        assertFalse(affichage.contains("Partie 1"));
    }

    /**
     * Teste le changement d'état
     */
    @Test
    void testChangerEtat() {
        SousTache st = new SousTache(1, "Petite tâche");
        st.setEtat("En cours");
        assertEquals("En cours", st.getEtat());
    }
}