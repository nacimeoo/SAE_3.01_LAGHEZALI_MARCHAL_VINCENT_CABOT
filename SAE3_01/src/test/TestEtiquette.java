import application.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour valider le Patron Décorateur (Etiquette et Description)
 */
public class TestEtiquette {

    private SousTache tacheDeBase;

    @BeforeEach
    void setUp() {
        tacheDeBase = new SousTache("Développer le Back-end");
        tacheDeBase.setEtat("En cours");
    }

    /**
     * Teste si l'Etiquette enveloppe correctement la tâche et ajoute son info
     */
    @Test
    void testAjoutEtiquette() {
        Etiquette tacheEtiquetee = new Etiquette(tacheDeBase, "nacime");

        assertEquals("Développer le Back-end", tacheEtiquetee.getNom());
        assertEquals("En cours", tacheEtiquetee.getEtat());

        assertEquals("nacime", tacheEtiquetee.getLibelle());

    }

    /**
     * Teste si la Description enveloppe correctement la tâche
     */
    @Test
    void testAjoutDescription() {
        Description tacheAvecDesc = new Description(tacheDeBase, "Corriger le bug #404");

        assertEquals("Développer le Back-end", tacheAvecDesc.getNom());

        String details = tacheAvecDesc.afficherDetails();
        assertTrue(details.contains("Description (Ajout) : Corriger le bug #404"));
    }

    /**
     * Teste le chaînage : Tâche -> Etiquette -> Description
     * C'est la force du patron Décorateur : combiner les ajouts.
     */
    @Test
    void testCumulDecorateurs() {
        TacheAbstraite tacheV1 = new Etiquette(tacheDeBase, "Prioritaire");

        TacheAbstraite tacheV2 = new Description(tacheV1, "Voir cahier des charges p.12");

        String details = tacheV2.afficherDetails();
        String affichageArbre = tacheV2.afficher("");

        assertTrue(details.contains("Développer le Back-end"));

        assertTrue(details.contains("Prioritaire"));
        assertTrue(affichageArbre.contains("[Etiquette: Prioritaire]"));

        assertTrue(details.contains("Voir cahier des charges p.12"));
    }

    /**
     * Vérifie que modifier l'état sur le décorateur modifie bien l'objet original
     */
    @Test
    void testModificationEtatViaDecorateur() {
        Etiquette tacheEtiquetee = new Etiquette(tacheDeBase, "Review");

        tacheEtiquetee.setEtat("Terminer");

        assertEquals("Terminer", tacheDeBase.getEtat());
        assertEquals("Terminer", tacheEtiquetee.getEtat());
    }
}