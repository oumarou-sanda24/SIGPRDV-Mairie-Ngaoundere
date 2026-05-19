package mairie.rdv.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================
 * Projet      : SIGPRDV - Système de Gestion de Prise de RDV
 * Mairie      : Mairie de Ngaoundéré
 * Cours       : Génie Logiciel 2025-2026
 * ============================================================
 * Classe      : RendezVous.java
 * Auteur      : NJIKAM LAMERE DAOUDA
 * Matricule   : 24B078FS
 * Branche Git : branche-daouda
 * Date        : 2025
 * ============================================================
 * Description :
 *   Classe principale représentant un rendez-vous pris par un
 *   citoyen auprès d'un service administratif de la mairie.
 *   Un RDV passe par plusieurs états : PLANIFIE → CONFIRME
 *   → TERMINE, ou peut être ANNULE à tout moment.
 * ============================================================
 */
public class RendezVous {

    // ============================================================
    // ATTRIBUTS
    // Chaque attribut est privé pour respecter l'encapsulation.
    // On y accède uniquement via les getters et setters.
    // ============================================================

    /** Identifiant unique du rendez-vous en base de données */
    private int id;

    /** Référence lisible du RDV, ex: RDV-2025-001234 */
    private String reference;

    /** Date du rendez-vous (année, mois, jour) */
    private LocalDate dateRdv;

    /** Heure de début du RDV, ex: "09:00" */
    private String heureDebut;

    /** Heure de fin du RDV, ex: "09:30" */
    private String heureFin;

    /**
     * Statut actuel du RDV.
     * Valeurs possibles : PLANIFIE, CONFIRME, ANNULE, TERMINE
     * Défini dans l'énumération StatutRdv.java (classe de FOKA WADOU)
     */
    private StatutRdv statut;

    /** Motif de la visite, ex: "Déclaration de naissance" */
    private String motif;

    /** Notes ou remarques supplémentaires sur le RDV */
    private String observations;

    /** Date et heure exactes de création du RDV dans le système */
    private LocalDateTime dateCreation;

    /** Date et heure de la dernière modification du RDV */
    private LocalDateTime dateModification;

    // ============================================================
    // ASSOCIATIONS (relations avec les autres classes du projet)
    // ============================================================

    /**
     * Le citoyen qui a pris ce rendez-vous.
     * Classe Citoyen codée par OUMAROU SANDA (24A524FS)
     */
    private Citoyen citoyen;

    /**
     * Le service administratif concerné par ce RDV.
     * Classe Service codée par NINO BRAYAN (24B066FS)
     */
    private Service service;

    /**
     * L'agent de la mairie affecté à ce rendez-vous.
     * Classe Agent codée par NGO KENG DOLORES (23B126FS)
     */
    private Agent agentAffecte;

    // ============================================================
    // CONSTRUCTEURS
    // Un constructeur permet de créer un objet RendezVous
    // ============================================================

    /**
     * Constructeur par défaut.
     * Initialise le statut à PLANIFIE et enregistre la date de création.
     * Utilisé quand on crée un RDV vide avant de remplir ses données.
     */
    public RendezVous() {
        this.statut       = StatutRdv.PLANIFIE;   // tout nouveau RDV commence en PLANIFIE
        this.dateCreation = LocalDateTime.now();   // on enregistre la date/heure actuelle
    }

    /**
     * Constructeur principal avec les informations essentielles.
     * Utilisé directement quand on connaît déjà le citoyen, le service,
     * la date et l'heure du rendez-vous.
     *
     * @param citoyen    Le citoyen qui prend le RDV
     * @param service    Le service administratif visé
     * @param dateRdv    La date choisie pour le RDV
     * @param heureDebut L'heure de début choisie, ex: "09:00"
     */
    public RendezVous(Citoyen citoyen, Service service,
                      LocalDate dateRdv, String heureDebut) {
        this();                         // appel du constructeur par défaut
        this.citoyen    = citoyen;
        this.service    = service;
        this.dateRdv    = dateRdv;
        this.heureDebut = heureDebut;
        this.reference  = genererReference(); // génération automatique de la référence
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // Ce sont les actions principales que peut faire un RDV.
    // Elles correspondent aux méthodes du diagramme de classes UML.
    // ============================================================

    /**
     * MÉTHODE : confirmer()
     * ----------------------
     * Confirme le rendez-vous.
     * Transition d'état : PLANIFIE → CONFIRME
     *
     * Règle métier : on ne peut confirmer que si le RDV est PLANIFIE.
     * Si le statut est autre, on lève une exception pour signaler l'erreur.
     */
    public void confirmer() {
        // Vérification : le RDV doit être en état PLANIFIE pour être confirmé
        if (this.statut != StatutRdv.PLANIFIE) {
            throw new IllegalStateException(
                "Impossible de confirmer un RDV au statut : " + this.statut);
        }
        this.statut           = StatutRdv.CONFIRME;     // changement du statut
        this.dateModification = LocalDateTime.now();    // on note la date de modification
        System.out.println("✔ RDV " + reference + " confirmé avec succès.");
    }

    /**
     * MÉTHODE : annuler(String motifAnnulation)
     * ------------------------------------------
     * Annule le rendez-vous en précisant la raison.
     * Transition d'état : PLANIFIE ou CONFIRME → ANNULE
     *
     * Règle métier :
     *   - Un RDV déjà ANNULE ne peut pas être annulé une 2ème fois.
     *   - Un RDV TERMINE ne peut plus être annulé.
     *
     * @param motifAnnulation La raison de l'annulation (obligatoire)
     */
    public void annuler(String motifAnnulation) {
        // Vérification : le RDV ne doit pas être déjà annulé
        if (this.statut == StatutRdv.ANNULE) {
            throw new IllegalStateException("Ce RDV est déjà annulé.");
        }
        // Vérification : un RDV terminé ne peut plus être annulé
        if (this.statut == StatutRdv.TERMINE) {
            throw new IllegalStateException("Un RDV terminé ne peut pas être annulé.");
        }
        this.statut           = StatutRdv.ANNULE;   // changement du statut
        // On enregistre la raison de l'annulation dans les observations
        this.observations     = "Annulé le " + LocalDate.now() + " | Motif : " + motifAnnulation;
        this.dateModification = LocalDateTime.now();
        System.out.println("✘ RDV " + reference + " annulé. Motif : " + motifAnnulation);
    }

    /**
     * MÉTHODE : terminer()
     * ---------------------
     * Marque le rendez-vous comme terminé après la visite du citoyen.
     * Transition d'état : CONFIRME ou PLANIFIE → TERMINE
     *
     * Règle métier : seul un RDV CONFIRME ou PLANIFIE peut être terminé.
     */
    public void terminer() {
        // Vérification : le RDV doit être CONFIRME ou PLANIFIE pour être terminé
        if (this.statut != StatutRdv.CONFIRME && this.statut != StatutRdv.PLANIFIE) {
            throw new IllegalStateException(
                "Impossible de terminer un RDV au statut : " + this.statut);
        }
        this.statut           = StatutRdv.TERMINE;      // changement du statut
        this.dateModification = LocalDateTime.now();    // on note la date de modification
        System.out.println("✔ RDV " + reference + " marqué comme terminé.");
    }

    /**
     * MÉTHODE PRIVÉE : genererReference()
     * -------------------------------------
     * Génère automatiquement une référence unique pour chaque RDV.
     * Format : RDV-AAAA-XXXXXX (ex: RDV-2025-042817)
     * Cette méthode est privée car elle est utilisée uniquement
     * en interne lors de la création du RDV.
     *
     * @return La référence générée sous forme de String
     */
    private String genererReference() {
        int annee  = LocalDate.now().getYear();         // récupère l'année actuelle
        int nombre = (int) (Math.random() * 999999);    // nombre aléatoire entre 0 et 999999
        return String.format("RDV-%d-%06d", annee, nombre); // format avec 6 chiffres minimum
    }

    // ============================================================
    // GETTERS ET SETTERS
    // Permettent d'accéder et de modifier les attributs privés
    // depuis les autres classes du projet.
    // ============================================================

    /** @return l'identifiant du RDV */
    public int getId() { return id; }
    /** @param id le nouvel identifiant */
    public void setId(int id) { this.id = id; }

    /** @return la référence du RDV, ex: RDV-2025-001234 */
    public String getReference() { return reference; }
    /** @param reference la nouvelle référence */
    public void setReference(String reference) { this.reference = reference; }

    /** @return la date du RDV */
    public LocalDate getDateRdv() { return dateRdv; }
    /** @param dateRdv la nouvelle date */
    public void setDateRdv(LocalDate dateRdv) { this.dateRdv = dateRdv; }

    /** @return l'heure de début du RDV */
    public String getHeureDebut() { return heureDebut; }
    /** @param heureDebut la nouvelle heure de début */
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    /** @return l'heure de fin du RDV */
    public String getHeureFin() { return heureFin; }
    /** @param heureFin la nouvelle heure de fin */
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    /** @return le statut actuel du RDV (PLANIFIE, CONFIRME, ANNULE, TERMINE) */
    public StatutRdv getStatut() { return statut; }
    /** @param statut le nouveau statut */
    public void setStatut(StatutRdv statut) { this.statut = statut; }

    /** @return le motif de la visite */
    public String getMotif() { return motif; }
    /** @param motif le nouveau motif */
    public void setMotif(String motif) { this.motif = motif; }

    /** @return les observations ou notes sur ce RDV */
    public String getObservations() { return observations; }
    /** @param observations les nouvelles observations */
    public void setObservations(String observations) { this.observations = observations; }

    /** @return la date et heure de création du RDV */
    public LocalDateTime getDateCreation() { return dateCreation; }

    /** @return la date et heure de la dernière modification */
    public LocalDateTime getDateModification() { return dateModification; }

    /** @return le citoyen associé à ce RDV */
    public Citoyen getCitoyen() { return citoyen; }
    /** @param citoyen le citoyen à associer */
    public void setCitoyen(Citoyen citoyen) { this.citoyen = citoyen; }

    /** @return le service administratif concerné */
    public Service getService() { return service; }
    /** @param service le service à associer */
    public void setService(Service service) { this.service = service; }

    /** @return l'agent affecté à ce RDV */
    public Agent getAgentAffecte() { return agentAffecte; }
    /** @param agentAffecte l'agent à affecter */
    public void setAgentAffecte(Agent agentAffecte) { this.agentAffecte = agentAffecte; }

    // ============================================================
    // MÉTHODE toString()
    // Permet d'afficher les informations du RDV sous forme de texte.
    // Utilisée par System.out.println(monRdv) par exemple.
    // ============================================================

    /**
     * Retourne une représentation textuelle du RDV.
     * Exemple de sortie :
     * [RDV-2025-042817] Citoyen: Jean DUPONT | Service: État Civil | Date: 2025-06-01 à 09:00 | Statut: PLANIFIE
     */
    @Override
    public String toString() {
        return String.format(
            "[%s] Citoyen: %s | Service: %s | Date: %s à %s | Statut: %s",
            reference,
            citoyen != null ? citoyen.getNomComplet() : "N/A",  // si citoyen existe, affiche son nom
            service != null ? service.getNom()        : "N/A",  // si service existe, affiche son nom
            dateRdv,
            heureDebut,
            statut
        )
    }
   public static void main(String[] args) {
        System.out.println("--- Test du modèle RendezVous ---");
        
        // 1. Création d'un rendez-vous vide (Constructeur par défaut)
        RendezVous rdvVide = new RendezVous();
        System.out.println("Nouveau RDV créé automatiquement avec la référence : " + rdvVide.getReference());
        System.out.println("Statut initial : " + rdvVide.getStatut()); // Doit afficher PLANIFIE

        System.out.println("\n--- Changement d'état ---");
        // 2. Test des méthodes métier (Changement de statut)
        try {
            rdvVide.confirmer(); // Passe de PLANIFIE à CONFIRME
            System.out.println("Statut après confirmation : " + rdvVide.getStatut());
            
            rdvVide.terminer();  // Passe de CONFIRME à TERMINE
            System.out.println("Statut final : " + rdvVide.getStatut());
        } catch (Exception e) {
            System.out.println("Erreur durant le test : " + e.getMessage());
        }
    }
}