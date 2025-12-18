import application.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la hiérarchie des tâches (Composite Pattern)
 */
class TestTache {

    /**
     * Vérifie qu'une SousTache agit comme une "feuille"
     */
    @Test
    void testSousTache() {
        SousTache st = new SousTache("dev");
        st.setId(10);
        TacheAbstraite autre = new SousTache( "");
        st.setId(11);

        assertFalse(st.ajouterDependance(autre), "Une sous-tâche ne doit pas accepter de dépendance");
        assertFalse(st.supprimerDependance(autre));
        assertTrue(st.afficher("").contains("Sous-tâche : dev"));
    }

    /**
     * Vérifie l'ajout d'une dépendance
     */
    @Test
    void testTacheMereAjoutDependanceOK() {
        TacheMere mere = new TacheMere("Grosse Tâche");
        mere.setId(20);
        SousTache fille = new SousTache("Partie 1");
        fille.setId(21);

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
        TacheMere mere = new TacheMere("Grosse Tâche");
        mere.setId(20);
        SousTache fille = new SousTache("Partie 1");
        fille.setId(21);

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
        SousTache st = new SousTache("Petite tâche");
        st.setEtat("En cours");
        assertEquals("En cours", st.getEtat());
    }
}