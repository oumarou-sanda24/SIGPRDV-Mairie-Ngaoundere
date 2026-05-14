package mairie.rdv.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent municipal de la Mairie de Ngaoundéré
 * Un agent est rattaché à un service et gère les RDV de ce service
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class Agent {

    // ======== ATTRIBUTS ========
    private int     id;
    private String  nom;
    private String  prenom;
    private String  email;
    private String  telephone;
    private String  matricule;    // Numéro matricule unique
    private String  motDePasse;
    private boolean actif;

    // Un agent est rattaché à un service
    private Service service;

    // Liste des RDV gérés par cet agent
    private List<RendezVous> rendezVousGeres;

    // ======== CONSTRUCTEURS ========
    public Agent() {
        this.actif             = true;
        this.rendezVousGeres   = new ArrayList<>();
    }

    public Agent(String nom, String prenom, String email, String matricule) {
        this();
        this.nom       = nom;
        this.prenom    = prenom;
        this.email     = email;
        this.matricule = matricule;
    }

    // ======== MÉTHODES MÉTIER ========

    /**
     * Valide (confirme) un rendez-vous
     */
    public void validerRdv(RendezVous rdv) {
        if (rdv == null) {
            throw new IllegalArgumentException("Le RDV ne peut pas être null.");
        }
        rdv.confirmer();
        System.out.println("RDV " + rdv.getReference() + " validé par " + getNomComplet());
    }

    /**
     * Retourne les RDV de l'agent pour une date donnée
     */
    public List<RendezVous> getRdvDuJour(LocalDate date) {
        List<RendezVous> liste = new ArrayList<>();
        for (RendezVous rdv : rendezVousGeres) {
            if (rdv.getDateRdv().equals(date)
             && rdv.getStatut() != StatutRdv.ANNULE) {
                liste.add(rdv);
            }
        }
        return liste;
    }

    /**
     * Retourne le nom complet de l'agent
     */
    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }

    // ======== GETTERS & SETTERS ========
    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getNom()             { return nom; }
    public void   setNom(String nom)   { this.nom = nom; }

    public String getPrenom()                { return prenom; }
    public void   setPrenom(String prenom)   { this.prenom = prenom; }

    public String getEmail()               { return email; }
    public void   setEmail(String email)   { this.email = email; }

    public String getTelephone()                   { return telephone; }
    public void   setTelephone(String telephone)   { this.telephone = telephone; }

    public String getMatricule()                   { return matricule; }
    public void   setMatricule(String matricule)   { this.matricule = matricule; }

    public String getMotDePasse()                    { return motDePasse; }
    public void   setMotDePasse(String motDePasse)   { this.motDePasse = motDePasse; }

    public boolean isActif()               { return actif; }
    public void    setActif(boolean actif) { this.actif = actif; }

    public Service getService()                { return service; }
    public void    setService(Service service) { this.service = service; }

    public List<RendezVous> getRendezVousGeres()              { return rendezVousGeres; }
    public void setRendezVousGeres(List<RendezVous> liste)    { this.rendezVousGeres = liste; }

    @Override
    public String toString() {
        return String.format("Agent[%d] %s | Matricule: %s | Service: %s",
                id, getNomComplet(), matricule,
                service != null ? service.getNom() : "Non affecté");
    }
}
