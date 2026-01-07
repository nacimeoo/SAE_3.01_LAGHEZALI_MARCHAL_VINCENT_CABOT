package application.vue;

import application.Colonne;
import application.TacheAbstraite;
import java.time.LocalDate;

public interface VueProjet {

    TacheAbstraite getTacheSelectionnee();
    void resetSelection();

    Colonne getColonneSelectionnee();


    default LocalDate getDateSelectionnee() {
        return null;
    }

    default boolean estVueListe() {
        return false;
    }
}
