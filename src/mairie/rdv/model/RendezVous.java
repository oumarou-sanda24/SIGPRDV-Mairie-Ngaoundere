package mairie.rdv.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité principale : Rendez-Vous à la Mairie de Ngaoundéré
 * Représente un rendez-vous entre un citoyen et un service administratif
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class RendezVous {

    // ======== ATTRIBUTS ========
    private int           id;
    private String        reference;         // Ex: RDV-2025-001234
    private LocalDate     dateRdv;
    private String        heureDebut;        // Ex: "09:00"
    private String        heureFin;          // Ex: "09:30"
    private StatutRdv     statut;
    private String        motif;             // Motif de la visite
    private String        observations;      // Notes supplémentaires
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Associations vers les autres entités
    private Citoyen citoyen;
    private Service service;
    private Agent   agentAffecte;

    // ======== CONSTRUCTEURS ========
    public RendezVous() {
        this.statut       = StatutRdv.PLANIFIE;
        this.dateCreation = LocalDateTime.now();
    }

    public RendezVous(Citoyen citoyen, Service service,
                      LocalDate dateRdv, String heureDebut) {
        this();
        this.citoyen    = citoyen;
        this.service    = service;
        this.dateRdv    = dateRdv;
        this.heureDebut = heureDebut;
        this.reference  = genererReference();
    }

    // ======== MÉTHODES MÉTIER ========

    /**
     * Confirme le rendez-vous (transition PLANIFIE → CONFIRME)
     */
    public void confirmer() {
        if (this.statut != StatutRdv.PLANIFIE) {
            throw new IllegalStateException(
                "Impossible de confirmer un RDV au statut : " + this.statut);
        }
        this.statut           = StatutRdv.CONFIRME;
        this.dateModification = LocalDateTime.now();
        System.out.println("RDV " + reference + " confirmé.");
    }

    /**
     * Annule le rendez-vous avec un motif obligatoire
     */
    public void annuler(String motifAnnulation) {
        if (this.statut == StatutRdv.ANNULE) {
            throw new IllegalStateException("Ce RDV est déjà annulé.");
        }
        if (this.statut == StatutRdv.TERMINE) {
            throw new IllegalStateException("Un RDV terminé ne peut pas être annulé.");
        }
        this.statut           = StatutRdv.ANNULE;
        this.observations     = "Annulé le " + LocalDate.now() + " | Motif : " + motifAnnulation;
        this.dateModification = LocalDateTime.now();
        System.out.println("RDV " + reference + " annulé. Motif : " + motifAnnulation);
    }

    /**
     * Marque le rendez-vous comme terminé (après la visite)
     */
    public void terminer() {
        if (this.statut != StatutRdv.CONFIRME && this.statut != StatutRdv.PLANIFIE) {
            throw new IllegalStateException(
                "Impossible de terminer un RDV au statut : " + this.statut);
        }
        this.statut           = StatutRdv.TERMINE;
        this.dateModification = LocalDateTime.now();
        System.out.println("RDV " + reference + " marqué comme terminé.");
    }

    /**
     * Génère une référence unique pour ce RDV
     * Format : RDV-AAAA-XXXXXX (ex: RDV-2025-042817)
     */
    private String genererReference() {
        int annee  = LocalDate.now().getYear();
        int nombre = (int) (Math.random() * 999999);
        return String.format("RDV-%d-%06d", annee, nombre);
    }

    // ======== GETTERS & SETTERS ========
    public int      getId()              { return id; }
    public void     setId(int id)        { this.id = id; }

    public String   getReference()       { return reference; }
    public void     setReference(String reference) { this.reference = reference; }

    public LocalDate getDateRdv()                    { return dateRdv; }
    public void      setDateRdv(LocalDate dateRdv)   { this.dateRdv = dateRdv; }

    public String   getHeureDebut()                      { return heureDebut; }
    public void     setHeureDebut(String heureDebut)     { this.heureDebut = heureDebut; }

    public String   getHeureFin()                    { return heureFin; }
    public void     setHeureFin(String heureFin)     { this.heureFin = heureFin; }

    public StatutRdv getStatut()                     { return statut; }
    public void      setStatut(StatutRdv statut)     { this.statut = statut; }

    public String   getMotif()               { return motif; }
    public void     setMotif(String motif)   { this.motif = motif; }

    public String   getObservations()                        { return observations; }
    public void     setObservations(String observations)     { this.observations = observations; }

    public LocalDateTime getDateCreation()       { return dateCreation; }
    public LocalDateTime getDateModification()   { return dateModification; }

    public Citoyen  getCitoyen()                   { return citoyen; }
    public void     setCitoyen(Citoyen citoyen)    { this.citoyen = citoyen; }

    public Service  getService()                   { return service; }
    public void     setService(Service service)    { this.service = service; }

    public Agent    getAgentAffecte()                        { return agentAffecte; }
    public void     setAgentAffecte(Agent agentAffecte)      { this.agentAffecte = agentAffecte; }

    @Override
    public String toString() {
        return String.format(
            "[%s] Citoyen: %s | Service: %s | Date: %s à %s | Statut: %s",
            reference,
            citoyen  != null ? citoyen.getNomComplet() : "N/A",
            service  != null ? service.getNom()        : "N/A",
            dateRdv,
            heureDebut,
            statut
        );
    }
}
