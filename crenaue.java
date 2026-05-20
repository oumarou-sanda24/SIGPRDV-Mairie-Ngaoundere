package mairie.rdv.model;

import java.time.LocalDate;

/*creneau.java fait par FOKA WADOU GAEL 24A617FS
 */
public class Creneau {

    // ======== ATTRIBUTS ========
    private int       id;
    private LocalDate date;
    private String    heureDebut;   // Format "08:00"
    private String    heureFin;     // Format "08:30"
    private boolean   disponible;

    // Associations
    private Service service;
    private Agent   agent;

    // ======== CONSTRUCTEURS ========
    public Creneau() {
        this.disponible = true;
    }

    public Creneau(LocalDate date, String heureDebut, String heureFin, Service service) {
        this();
        this.date       = date;
        this.heureDebut = heureDebut;
        this.heureFin   = heureFin;
        this.service    = service;
    }

    // ======== MÉTHODES MÉTIER ========

    /**
     * Bloque le créneau (après réservation d'un RDV)
     */
    public void bloquer() {
        this.disponible = false;
    }

    /**
     * Libère le créneau (après annulation d'un RDV)
     */
    public void liberer() {
        this.disponible = true;
    }

    /**
     * Représentation lisible du créneau
     */
    public String getLibelle() {
        return date + " de " + heureDebut + " à " + heureFin;
    }

    // ======== GETTERS & SETTERS ========
    public int      getId()              { return id; }
    public void     setId(int id)        { this.id = id; }

    public LocalDate getDate()               { return date; }
    public void      setDate(LocalDate date) { this.date = date; }

    public String   getHeureDebut()                  { return heureDebut; }
    public void     setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String   getHeureFin()                { return heureFin; }
    public void     setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public boolean  isDisponible()                   { return disponible; }
    public void     setDisponible(boolean disponible){ this.disponible = disponible; }

    public Service  getService()                 { return service; }
    public void     setService(Service service)  { this.service = service; }

    public Agent    getAgent()               { return agent; }
    public void     setAgent(Agent agent)    { this.agent = agent; }

    @Override
    public String toString() {
        return String.format("Créneau[%d] %s | Dispo: %s",
                id, getLibelle(), disponible ? "Oui" : "Non");
    }
}
