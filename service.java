package mairie.rdv.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service administratif proposé par la Mairie de Ngaoundéré
 * Exemples : Déclaration de naissance, Légalisation, Acte de mariage...
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class Service {

    // ======== ATTRIBUTS ========
    private int     id;
    private String  nom;
    private String  description;
    private String  categorie;            // Ex: "État Civil", "Urbanisme"
    private int     dureeMinutes;         // Durée estimée du RDV
    private boolean disponible;
    private String  documentsRequis;      // Documents à apporter
    private int     capaciteJournaliere;  // Nb max de RDV par jour

    // Associations
    private List<Agent>      agents;
    private List<Creneau>    creneaux;
    private List<RendezVous> rendezVousList;

    // ======== CONSTRUCTEURS ========
    public Service() {
        this.disponible      = true;
        this.dureeMinutes    = 30;
        this.agents          = new ArrayList<>();
        this.creneaux        = new ArrayList<>();
        this.rendezVousList  = new ArrayList<>();
    }

    public Service(String nom, String categorie, int dureeMinutes) {
        this();
        this.nom          = nom;
        this.categorie    = categorie;
        this.dureeMinutes = dureeMinutes;
    }

    // ======== MÉTHODES MÉTIER ========

    /**
     * Vérifie si le service peut encore accepter des RDV un jour donné
     */
    public boolean peutAccepterRdv(LocalDate date) {
        long count = 0;
        for (RendezVous rdv : rendezVousList) {
            if (rdv.getDateRdv().equals(date)
             && rdv.getStatut() != StatutRdv.ANNULE) {
                count++;
            }
        }
        return count < capaciteJournaliere;
    }

    /**
     * Retourne les créneaux disponibles pour une date donnée
     */
    public List<Creneau> getCreneauxDisponibles(LocalDate date) {
        List<Creneau> dispos = new ArrayList<>();
        for (Creneau c : creneaux) {
            if (c.isDisponible() && c.getDate().equals(date)) {
                dispos.add(c);
            }
        }
        return dispos;
    }

    /**
     * Ajoute un agent à ce service
     */
    public void ajouterAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }

    // ======== GETTERS & SETTERS ========
    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getNom()             { return nom; }
    public void   setNom(String nom)   { this.nom = nom; }

    public String getDescription()                     { return description; }
    public void   setDescription(String description)   { this.description = description; }

    public String getCategorie()                   { return categorie; }
    public void   setCategorie(String categorie)   { this.categorie = categorie; }

    public int    getDureeMinutes()                      { return dureeMinutes; }
    public void   setDureeMinutes(int dureeMinutes)      { this.dureeMinutes = dureeMinutes; }

    public boolean isDisponible()                  { return disponible; }
    public void    setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getDocumentsRequis()                       { return documentsRequis; }
    public void   setDocumentsRequis(String documentsRequis) { this.documentsRequis = documentsRequis; }

    public int    getCapaciteJournaliere()                         { return capaciteJournaliere; }
    public void   setCapaciteJournaliere(int capaciteJournaliere)  { this.capaciteJournaliere = capaciteJournaliere; }

    public List<Agent>      getAgents()          { return agents; }
    public List<Creneau>    getCreneaux()         { return creneaux; }
    public List<RendezVous> getRendezVousList()   { return rendezVousList; }

    @Override
    public String toString() {
        return String.format("Service[%d] %s | Catégorie: %s | Durée: %d min",
                id, nom, categorie, dureeMinutes);
    }
}
