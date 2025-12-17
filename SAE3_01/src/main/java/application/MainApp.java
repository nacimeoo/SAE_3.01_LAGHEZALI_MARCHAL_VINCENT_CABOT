package application;

import application.vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.DAO.*;

import java.util.Date;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            ProjetService service = new ProjetService();
            Projet projet = chargerProjetComplet(1);

            if (projet == null) {
                System.out.println("Projet non trouvé, création d'un nouveau...");
                projet = service.creerProjet("Mon Nouveau Projet", new Date());
                service.ajouterColonne(projet, new Colonne("A faire"));
                service.ajouterColonne(projet, new Colonne("En cours"));
            }

            VueKanban root = new VueKanban(projet, service);

            Scene scene = new Scene(root, 1000, 600);
            primaryStage.setTitle("FRIDAY - " + projet.getNom());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Projet chargerProjetComplet(int idProjet) {
        try {
            ProjetDAOImpl projetDAO = new ProjetDAOImpl();
            TacheDAOImpl tacheDAO = new TacheDAOImpl();

            Projet p = projetDAO.getProjetById(idProjet);
            if (p == null) return null;

            List<Colonne> colonnes = projetDAO.getColonnesByProjetId(p.getId());

            for (Colonne col : colonnes) {
                List<TacheAbstraite> taches = tacheDAO.getTachesByColonneId(col.getId());
                for(TacheAbstraite t : taches) {
                    col.ajouterTache(t);
                }
                p.getColonnes().add(col);
            }

            return p;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}