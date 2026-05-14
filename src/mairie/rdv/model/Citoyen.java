package mairie.rdv.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un Citoyen souhaitant prendre un RDV à la mairie
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class Citoyen {

    // ======== ATTRIBUTS ========
    private int      id;
    private String   nom;
    private String   prenom;
    private String   email;
    private String   telephone;
    private String   cni;              // Numéro Carte Nationale d'Identité
    private String   adresse;
    private LocalDate dateNaissance;
    private String   motDePasse;       // En production : hashé avec BCrypt
    private boolean  actif;

    // Un citoyen peut avoir plusieurs rendez-vous
    private List<RendezVous> rendezVousList;

    // ======== CONSTRUCTEURS ========
    public Citoyen() {
        this.actif           = true;
        this.rendezVousList  = new ArrayList<>();
    }

    public Citoyen(String nom, String prenom, String email, String telephone) {
        this();
        this.nom       = nom;
        this.prenom    = prenom;
        this.email     = email;
        this.telephone = telephone;
    }

    // ======== MÉTHODES MÉTIER ========

    /**
     * Retourne le nom complet (prénom + NOM)
     */
    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }

    /**
     * Ajoute un rendez-vous à la liste du citoyen
     */
    public void ajouterRendezVous(RendezVous rdv) {
        if (rdv != null) {
            this.rendezVousList.add(rdv);
        }
    }

    /**
     * Retourne uniquement les RDV actifs (PLANIFIE ou CONFIRME)
     */
    public List<RendezVous> getRendezVousActifs() {
        List<RendezVous> actifs = new ArrayList<>();
        for (RendezVous rdv : rendezVousList) {
            if (rdv.getStatut() == StatutRdv.PLANIFIE
             || rdv.getStatut() == StatutRdv.CONFIRME) {
                actifs.add(rdv);
            }
        }
        return actifs;
    }

    /**
     * Vérifie si le citoyen a déjà un RDV à une date donnée
     */
    public boolean aDejaRdvLe(LocalDate date) {
        for (RendezVous rdv : rendezVousList) {
            if (rdv.getDateRdv().equals(date)
             && rdv.getStatut() != StatutRdv.ANNULE) {
                return true;
            }
        }
        return false;
    }

    // ======== GETTERS & SETTERS ========
    public int      getId()            { return id; }
    public void     setId(int id)      { this.id = id; }

    public String   getNom()           { return nom; }
    public void     setNom(String nom) { this.nom = nom; }

    public String   getPrenom()              { return prenom; }
    public void     setPrenom(String prenom) { this.prenom = prenom; }

    public String   getEmail()               { return email; }
    public void     setEmail(String email)   { this.email = email; }

    public String   getTelephone()                   { return telephone; }
    public void     setTelephone(String telephone)   { this.telephone = telephone; }

    public String   getCni()             { return cni; }
    public void     setCni(String cni)   { this.cni = cni; }

    public String   getAdresse()                 { return adresse; }
    public void     setAdresse(String adresse)   { this.adresse = adresse; }

    public LocalDate getDateNaissance()                      { return dateNaissance; }
    public void      setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String   getMotDePasse()                  { return motDePasse; }
    public void     setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public boolean  isActif()              { return actif; }
    public void     setActif(boolean actif){ this.actif = actif; }

    public List<RendezVous> getRendezVousList() { return rendezVousList; }
    public void setRendezVousList(List<RendezVous> liste) { this.rendezVousList = liste; }

    @Override
    public String toString() {
        return String.format("Citoyen[%d] %s | Email: %s | Tel: %s",
                id, getNomComplet(), email, telephone);
    }
}
