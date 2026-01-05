package application.vue;

import application.Colonne;
import application.TacheAbstraite;

public interface VueProjet {

    TacheAbstraite getTacheSelectionnee();
    void resetSelection();


    Colonne getColonneSelectionnee();

}
