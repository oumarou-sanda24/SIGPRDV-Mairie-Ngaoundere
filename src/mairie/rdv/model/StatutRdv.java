package mairie.rdv.model;

/**
 * Enumération des statuts possibles d'un Rendez-Vous
 * Mairie de Ngaoundéré - SIGPRDV
 */
public enum StatutRdv {

    PLANIFIE("Planifié - En attente de confirmation"),
    CONFIRME("Confirmé par l'administration"),
    ANNULE("Annulé"),
    TERMINE("RDV effectué");

    private final String libelle;

    StatutRdv(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
