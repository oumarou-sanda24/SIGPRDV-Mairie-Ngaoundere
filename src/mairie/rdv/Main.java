package mairie.rdv;

/**
 * ============================================================
 * Classe         : Main
 * Description    : Point d'entrée du système SIGPRDV
 * Implémentée par: OUMAROU SANDA
 * Matricule      : 24A524FS
 * Branche Git    : branche-oumarou
 * Projet         : SIGPRDV — Mairie de Ngaoundéré
 * Année          : 2024 - 2025
 * ============================================================
 */
import mairie.rdv.model.*;
import java.time.LocalDate;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  SIGPRDV — Mairie de Ngaoundéré");
        System.out.println("  Système de Gestion des Rendez-Vous");
        System.out.println("  Implémenté par : OUMAROU SANDA - 24A524FS");
        System.out.println("=================================================\n");

        // 1. CRÉER LES SERVICES DE LA MAIRIE
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
        System.out.println("Service créé : " + legalisation);

        Service permisConstr = new Service(
            "Permis de construire", "Urbanisme", 60);
        permisConstr.setId(3);
        permisConstr.setCapaciteJournaliere(8);
        System.out.println("Service créé : " + permisConstr);

        // 2. CRÉER DES CITOYENS
        System.out.println("\n--- Création des citoyens ---");

        Citoyen citoyen1 = new Citoyen(
            "OUMAROU", "Sanda", "elsandos10@email.com", "655161820");
        citoyen1.setId(1);
        citoyen1.setCni("000123456");
        System.out.println("Citoyen créé : " + citoyen1);

        Citoyen citoyen2 = new Citoyen(
            "BELLO", "Bouba", "boubabello@email.com", "699334455");
        citoyen2.setId(2);
        citoyen2.setCni("000789012");
        System.out.println("Citoyen créé : " + citoyen2);

        // 3. CRÉER UN AGENT
        System.out.println("\n--- Création des agents ---");

        Agent agent1 = new Agent(
            "YAYA", "Emmanuel", "emmanuelyaya@mairie-ngaoundere.cm", "MAT-001");
        agent1.setId(1);
        agent1.setService(declarationNaissance);
        System.out.println("Agent créé : " + agent1);

        // 4. PRENDRE DES RENDEZ-VOUS
        System.out.println("\n--- Prise de rendez-vous ---");

        LocalDate demain      = LocalDate.now().plusDays(1);
        LocalDate apresdemain = LocalDate.now().plusDays(2);

        RendezVous rdv1 = new RendezVous(
            citoyen1, declarationNaissance, demain, "09:00");
        rdv1.setId(1);
        rdv1.setMotif("Déclaration de naissance de mon fils Moussa");
        rdv1.setHeureFin("09:30");
        citoyen1.ajouterRendezVous(rdv1);
        System.out.println("RDV créé : " + rdv1);

        RendezVous rdv2 = new RendezVous(
            citoyen2, legalisation, apresdemain, "10:00");
        rdv2.setId(2);
        rdv2.setMotif("Légalisation de diplôme pour dossier emploi");
        rdv2.setHeureFin("10:20");
        citoyen2.ajouterRendezVous(rdv2);
        System.out.println("RDV créé : " + rdv2);

        // 5. CONFIRMER UN RDV
        System.out.println("\n--- Confirmation par l'agent ---");
        try {
            rdv1.confirmer();
            System.out.println("Statut RDV1 : " + rdv1.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 6. ANNULER UN RDV
        System.out.println("\n--- Annulation d'un RDV ---");
        try {
            rdv2.annuler("Empêchement professionnel");
            System.out.println("Statut RDV2 : " + rdv2.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 7. TESTER LES RÈGLES MÉTIER
        System.out.println("\n--- Test des règles métier ---");
        try {
            rdv2.confirmer();
        } catch (IllegalStateException e) {
            System.out.println("Règle respectée : " + e.getMessage());
        }

        System.out.println("Conflit le " + demain + " ? "
            + citoyen1.aDejaRdvLe(demain));

        // 8. TERMINER UN RDV
        System.out.println("\n--- Terminer un RDV ---");
        try {
            rdv1.terminer();
            System.out.println("Statut RDV1 : " + rdv1.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // 9. AFFICHER LES RDV ACTIFS
        System.out.println("\n--- RDV actifs d'Alioum ---");
        List<RendezVous> actifs = citoyen1.getRendezVousActifs();
        if (actifs.isEmpty()) {
            System.out.println("Aucun RDV actif.");
        } else {
            for (RendezVous r : actifs) {
                System.out.println("  > " + r);
            }
        }

        // 10. RÉCAPITULATIF DES SERVICES
        System.out.println("\n--- Récapitulatif des services ---");
        Service[] services = {declarationNaissance, legalisation, permisConstr};
        for (Service s : services) {
            System.out.printf(
                "  %-30s | Durée: %2d min | Capacité/jour: %2d | Dispo: %s%n",
                s.getNom(), s.getDureeMinutes(),
                s.getCapaciteJournaliere(),
                s.isDisponible() ? "OUI" : "NON");
        }

        System.out.println("\n=================================================");
        System.out.println("  Tests terminés avec succès !");
        System.out.println("  OUMAROU SANDA - 24A524FS");
        System.out.println("=================================================");
    }
}
