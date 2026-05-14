package mairie.rdv;

import mairie.rdv.model.*;
import mairie.rdv.service.GestionRdvService;
import mairie.rdv.dao.RendezVousDAO;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================
 * CLASSE PRINCIPALE — TEST SANS BASE DE DONNÉES
 * Mairie de Ngaoundéré - SIGPRDV
 *
 * Cette classe teste toutes les fonctionnalités du système
 * en mémoire, sans connexion JDBC.
 *
 * Pour lancer dans Eclipse :
 *   Clic droit sur Main.java > Run As > Java Application
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  SIGPRDV — Mairie de Ngaoundéré");
        System.out.println("  Système de Gestion des Rendez-Vous");
        System.out.println("=================================================\n");

        // -----------------------------------------------
        // 1. CRÉER LES SERVICES DE LA MAIRIE
        // -----------------------------------------------
        System.out.println("--- Création des services ---");

        Service declarationNaissance = new Service(
            "Déclaration de naissance", "État Civil", 30);
        declarationNaissance.setId(1);
        declarationNaissance.setCapaciteJournaliere(25);
        declarationNaissance.setDocumentsRequis(
            "Certificat médical, CNI des parents, Acte de mariage");
        System.out.println("Service créé : " + declarationNaissance);

        Service legalisation = new Service(
            "Légalisation de documents", "État Civil", 20);
        legalisation.setId(2);
        legalisation.setCapaciteJournaliere(40);
        legalisation.setDocumentsRequis("Document original + photocopie, CNI");
        System.out.println("Service créé : " + legalisation);

        Service permisConstr = new Service(
            "Permis de construire", "Urbanisme", 60);
        permisConstr.setId(3);
        permisConstr.setCapaciteJournaliere(8);
        System.out.println("Service créé : " + permisConstr);

        // -----------------------------------------------
        // 2. CRÉER DES CITOYENS
        // -----------------------------------------------
        System.out.println("\n--- Création des citoyens ---");

        Citoyen citoyen1 = new Citoyen("MBODJ", "Alioum", "alioum@email.com", "677001122");
        citoyen1.setId(1);
        citoyen1.setCni("000123456");
        citoyen1.setAdresse("Quartier Baladji, Ngaoundéré");
        System.out.println("Citoyen créé : " + citoyen1);

        Citoyen citoyen2 = new Citoyen("BELLO", "Fadimatou", "fadima@email.com", "699334455");
        citoyen2.setId(2);
        citoyen2.setCni("000789012");
        System.out.println("Citoyen créé : " + citoyen2);

        // -----------------------------------------------
        // 3. CRÉER UN AGENT MUNICIPAL
        // -----------------------------------------------
        System.out.println("\n--- Création des agents ---");

        Agent agent1 = new Agent("HAMADOU", "Saidou", "saidou@mairie-ngaoundere.cm", "MAT-001");
        agent1.setId(1);
        agent1.setService(declarationNaissance);
        System.out.println("Agent créé : " + agent1);

        // -----------------------------------------------
        // 4. PRENDRE DES RENDEZ-VOUS (sans base de données)
        // -----------------------------------------------
        System.out.println("\n--- Prise de rendez-vous ---");

        LocalDate demain = LocalDate.now().plusDays(1);
        LocalDate apresdemain = LocalDate.now().plusDays(2);

        // RDV 1 : Alioum pour déclaration de naissance demain
        RendezVous rdv1 = new RendezVous(citoyen1, declarationNaissance, demain, "09:00");
        rdv1.setId(1);
        rdv1.setMotif("Déclaration de naissance de mon fils Moussa");
        rdv1.setHeureFin("09:30");
        citoyen1.ajouterRendezVous(rdv1);
        System.out.println("RDV créé : " + rdv1);

        // RDV 2 : Fadimatou pour légalisation après-demain
        RendezVous rdv2 = new RendezVous(citoyen2, legalisation, apresdemain, "10:00");
        rdv2.setId(2);
        rdv2.setMotif("Légalisation de diplôme pour dossier emploi");
        rdv2.setHeureFin("10:20");
        citoyen2.ajouterRendezVous(rdv2);
        System.out.println("RDV créé : " + rdv2);

        // -----------------------------------------------
        // 5. CONFIRMER UN RDV (par l'agent)
        // -----------------------------------------------
        System.out.println("\n--- Confirmation par l'agent ---");
        try {
            rdv1.confirmer();
            System.out.println("Statut RDV1 après confirmation : " + rdv1.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur confirmation : " + e.getMessage());
        }

        // -----------------------------------------------
        // 6. TESTER L'ANNULATION
        // -----------------------------------------------
        System.out.println("\n--- Annulation d'un RDV ---");
        try {
            rdv2.annuler("Empêchement professionnel");
            System.out.println("Statut RDV2 après annulation : " + rdv2.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur annulation : " + e.getMessage());
        }

        // -----------------------------------------------
        // 7. TESTER LES RÈGLES MÉTIER
        // -----------------------------------------------
        System.out.println("\n--- Test des règles métier ---");

        // Tentative de confirmer un RDV déjà annulé
        try {
            rdv2.confirmer();
        } catch (IllegalStateException e) {
            System.out.println("Règle respectée : " + e.getMessage());
        }

        // Tentative de conflit de date
        System.out.println("\nAlioum a-t-il déjà un RDV le " + demain + " ? "
            + citoyen1.aDejaRdvLe(demain));
        System.out.println("Alioum a-t-il déjà un RDV le " + apresdemain + " ? "
            + citoyen1.aDejaRdvLe(apresdemain));

        // -----------------------------------------------
        // 8. TERMINER UN RDV
        // -----------------------------------------------
        System.out.println("\n--- Terminer un RDV (après visite) ---");
        try {
            rdv1.terminer();
            System.out.println("Statut RDV1 après visite : " + rdv1.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // -----------------------------------------------
        // 9. AFFICHER LES RDV ACTIFS DU CITOYEN
        // -----------------------------------------------
        System.out.println("\n--- RDV actifs d'Alioum ---");
        List<RendezVous> actifs = citoyen1.getRendezVousActifs();
        if (actifs.isEmpty()) {
            System.out.println("Aucun RDV actif pour " + citoyen1.getNomComplet());
        } else {
            for (RendezVous r : actifs) {
                System.out.println("  > " + r);
            }
        }

        // -----------------------------------------------
        // 10. AFFICHER LE STATUT DES SERVICES
        // -----------------------------------------------
        System.out.println("\n--- Récapitulatif des services ---");
        Service[] services = {declarationNaissance, legalisation, permisConstr};
        for (Service s : services) {
            System.out.printf("  %-30s | Durée: %2d min | Capacité/jour: %2d | Dispo: %s%n",
                s.getNom(), s.getDureeMinutes(),
                s.getCapaciteJournaliere(),
                s.isDisponible() ? "OUI" : "NON");
        }

        System.out.println("\n=================================================");
        System.out.println("  Tests terminés avec succès !");
        System.out.println("=================================================");
    }
}
