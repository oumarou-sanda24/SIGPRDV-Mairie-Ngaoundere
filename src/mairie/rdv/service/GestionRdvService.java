package mairie.rdv.service;

import mairie.rdv.dao.RendezVousDAO;
import mairie.rdv.exception.RdvException;
import mairie.rdv.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Service métier — Gestion des Rendez-Vous
 * Centralise toutes les règles métier du système SIGPRDV
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class GestionRdvService {

    private final RendezVousDAO rdvDAO;

    public GestionRdvService(RendezVousDAO rdvDAO) {
        this.rdvDAO = rdvDAO;
    }

    // ============================================================
    //  PRENDRE UN RENDEZ-VOUS
    // ============================================================

    /**
     * Crée un nouveau rendez-vous après vérification des règles métier :
     *   1. Le service doit être disponible
     *   2. La capacité journalière ne doit pas être dépassée
     *   3. Le citoyen ne doit pas avoir déjà un RDV ce jour-là
     *
     * @param citoyen   Le citoyen demandeur
     * @param service   Le service administratif souhaité
     * @param date      La date choisie
     * @param heure     L'heure de début choisie (ex: "09:00")
     * @param motif     Motif de la visite
     * @return          Le RendezVous créé et persisté
     */
    public RendezVous prendreRendezVous(Citoyen citoyen,
                                         Service service,
                                         LocalDate date,
                                         String heure,
                                         String motif)
                                         throws Exception {

        // --- Règle 1 : le service doit exister et être actif ---
        if (service == null || !service.isDisponible()) {
            throw new RdvException("Le service demandé n'est pas disponible.");
        }

        // --- Règle 2 : vérifier la capacité journalière ---
        if (!service.peutAccepterRdv(date)) {
            throw new RdvException(
                "Plus de créneaux disponibles pour le service '"
                + service.getNom() + "' le " + date + ".");
        }

        // --- Règle 3 : le citoyen ne doit pas avoir de conflit ---
        if (citoyen.aDejaRdvLe(date)) {
            throw new RdvException(
                "Vous avez déjà un rendez-vous planifié le " + date
                + ". Veuillez choisir une autre date.");
        }

        // --- Création du rendez-vous ---
        RendezVous rdv = new RendezVous(citoyen, service, date, heure);
        rdv.setMotif(motif);

        // Calcul automatique de l'heure de fin
        rdv.setHeureFin(calculerHeureFin(heure, service.getDureeMinutes()));

        // Persistance en base de données
        rdvDAO.inserer(rdv);

        // Mise à jour de la liste côté objet citoyen
        citoyen.ajouterRendezVous(rdv);

        System.out.println("=== RDV CRÉÉ AVEC SUCCÈS ===");
        System.out.println(rdv);

        return rdv;
    }

    // ============================================================
    //  ANNULER UN RENDEZ-VOUS
    // ============================================================

    /**
     * Annule un rendez-vous en appliquant la règle des 48h
     *
     * @param rdv              Le rendez-vous à annuler
     * @param citoyenDemandeur Le citoyen qui demande l'annulation
     * @param motif            Le motif de l'annulation
     */
    public void annulerRendezVous(RendezVous rdv,
                                   Citoyen citoyenDemandeur,
                                   String motif)
                                   throws RdvException, Exception {

        // Vérifier que le RDV existe
        if (rdv == null) {
            throw new RdvException("Rendez-vous introuvable.");
        }

        // Vérifier que le citoyen est bien le propriétaire
        if (rdv.getCitoyen().getId() != citoyenDemandeur.getId()) {
            throw new RdvException("Accès non autorisé : ce RDV ne vous appartient pas.");
        }

        // Règle métier : annulation possible au moins 48h avant
        LocalDate limiteAnnulation = rdv.getDateRdv().minusDays(2);
        if (LocalDate.now().isAfter(limiteAnnulation)) {
            throw new RdvException(
                "L'annulation doit être effectuée au moins 48h avant le RDV. "
                + "Date limite dépassée (" + limiteAnnulation + ").");
        }

        // Annulation via l'entité
        rdv.annuler(motif);

        // Mise à jour en base de données
        rdvDAO.annuler(rdv.getId(), motif);

        System.out.println("RDV " + rdv.getReference() + " annulé avec succès.");
    }

    // ============================================================
    //  CONFIRMER UN RENDEZ-VOUS (par un agent)
    // ============================================================

    /**
     * Un agent confirme un rendez-vous planifié
     */
    public void confirmerRendezVous(RendezVous rdv, Agent agent)
            throws RdvException, Exception {

        if (rdv == null) {
            throw new RdvException("Rendez-vous introuvable.");
        }

        // Confirmer via l'entité (lève une exception si statut invalide)
        rdv.confirmer();

        // Affecter l'agent si pas encore fait
        if (rdv.getAgentAffecte() == null) {
            rdv.setAgentAffecte(agent);
            rdvDAO.affecterAgent(rdv.getId(), agent.getId());
        }

        // Mettre à jour le statut en base
        rdvDAO.mettreAJourStatut(rdv.getId(), StatutRdv.CONFIRME);

        System.out.println("RDV " + rdv.getReference()
            + " confirmé par l'agent " + agent.getNomComplet());
    }

    // ============================================================
    //  TERMINER UN RENDEZ-VOUS (après la visite)
    // ============================================================

    /**
     * Marque un rendez-vous comme terminé après la visite du citoyen
     */
    public void terminerRendezVous(RendezVous rdv) throws Exception {
        if (rdv == null) {
            throw new RdvException("Rendez-vous introuvable.");
        }
        rdv.terminer();
        rdvDAO.mettreAJourStatut(rdv.getId(), StatutRdv.TERMINE);
        System.out.println("RDV " + rdv.getReference() + " marqué comme terminé.");
    }

    // ============================================================
    //  LISTER LES RENDEZ-VOUS
    // ============================================================

    /**
     * Retourne tous les RDV d'un citoyen
     */
    public List<RendezVous> getMesRendezVous(Citoyen citoyen)
            throws Exception {
        return rdvDAO.trouverParCitoyen(citoyen.getId());
    }

    /**
     * Retourne les RDV d'un service pour une date donnée
     */
    public List<RendezVous> getRdvDuJour(Service service, LocalDate date)
            throws Exception {
        return rdvDAO.trouverParServiceEtDate(service.getId(), date);
    }

    // ============================================================
    //  UTILITAIRES PRIVÉS
    // ============================================================

    /**
     * Calcule l'heure de fin en ajoutant la durée à l'heure de début
     * Ex: "09:00" + 30 min → "09:30"
     */
    private String calculerHeureFin(String heureDebut, int dureeMinutes) {
        try {
            String[] parts = heureDebut.split(":");
            int heures  = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            minutes += dureeMinutes;
            heures  += minutes / 60;
            minutes  = minutes % 60;
            heures   = heures % 24;

            return String.format("%02d:%02d", heures, minutes);
        } catch (Exception e) {
            return heureDebut; // En cas d'erreur, retourner l'heure de début
        }
    }
}
