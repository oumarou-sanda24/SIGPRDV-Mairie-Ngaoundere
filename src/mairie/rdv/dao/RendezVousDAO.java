package mairie.rdv.dao;

import mairie.rdv.model.RendezVous;
import mairie.rdv.model.StatutRdv;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la table rendez_vous
 * Gère toutes les opérations CRUD sur les RDV en base de données
 * Mairie de Ngaoundéré - SIGPRDV
 *
 * PRÉREQUIS : ajouter le JAR MySQL Connector/J dans le classpath Eclipse
 *   Télécharger : https://dev.mysql.com/downloads/connector/j/
 *   Dans Eclipse : clic droit projet > Build Path > Add External JARs
 */
public class RendezVousDAO {

    private final Connection connection;

    public RendezVousDAO(Connection connection) {
        this.connection = connection;
    }

    // ============================================================
    //  CREATE
    // ============================================================

    /**
     * Insère un nouveau rendez-vous en base de données
     * @param rdv Le rendez-vous à insérer (sans id)
     * @return Le même objet avec l'id auto-généré renseigné
     */
    public RendezVous inserer(RendezVous rdv) throws SQLException {
        String sql =
            "INSERT INTO rendez_vous " +
            "(reference, date_rdv, heure_debut, heure_fin, statut, motif, " +
            " citoyen_id, service_id, agent_id, date_creation) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        PreparedStatement ps = connection.prepareStatement(
            sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, rdv.getReference());
        ps.setDate(  2, Date.valueOf(rdv.getDateRdv()));
        ps.setString(3, rdv.getHeureDebut());
        ps.setString(4, rdv.getHeureFin());
        ps.setString(5, rdv.getStatut().name());
        ps.setString(6, rdv.getMotif());
        ps.setInt(   7, rdv.getCitoyen().getId());
        ps.setInt(   8, rdv.getService().getId());

        if (rdv.getAgentAffecte() != null) {
            ps.setInt(9, rdv.getAgentAffecte().getId());
        } else {
            ps.setNull(9, Types.INTEGER);
        }

        int lignes = ps.executeUpdate();

        if (lignes > 0) {
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                rdv.setId(keys.getInt(1));
            }
        }

        ps.close();
        return rdv;
    }

    // ============================================================
    //  READ
    // ============================================================

    /**
     * Recherche un rendez-vous par son identifiant
     * @return Le RendezVous trouvé, ou null si inexistant
     */
    public RendezVous trouverParId(int id) throws SQLException {
        String sql = "SELECT * FROM rendez_vous WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        RendezVous rdv = null;
        if (rs.next()) {
            rdv = mapperResultat(rs);
        }

        rs.close();
        ps.close();
        return rdv;
    }

    /**
     * Recherche un rendez-vous par sa référence unique (ex: RDV-2025-001234)
     */
    public RendezVous trouverParReference(String reference) throws SQLException {
        String sql = "SELECT * FROM rendez_vous WHERE reference = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, reference);

        ResultSet rs = ps.executeQuery();
        RendezVous rdv = null;
        if (rs.next()) {
            rdv = mapperResultat(rs);
        }

        rs.close();
        ps.close();
        return rdv;
    }

    /**
     * Retourne tous les rendez-vous d'un citoyen (triés par date décroissante)
     */
    public List<RendezVous> trouverParCitoyen(int citoyenId) throws SQLException {
        String sql =
            "SELECT * FROM rendez_vous " +
            "WHERE citoyen_id = ? " +
            "ORDER BY date_rdv DESC, heure_debut ASC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, citoyenId);

        List<RendezVous> liste = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            liste.add(mapperResultat(rs));
        }

        rs.close();
        ps.close();
        return liste;
    }

    /**
     * Retourne tous les RDV d'un service pour une date donnée (hors annulés)
     */
    public List<RendezVous> trouverParServiceEtDate(int serviceId,
                                                     LocalDate date)
                                                     throws SQLException {
        String sql =
            "SELECT * FROM rendez_vous " +
            "WHERE service_id = ? AND date_rdv = ? AND statut <> 'ANNULE' " +
            "ORDER BY heure_debut ASC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt( 1, serviceId);
        ps.setDate(2, Date.valueOf(date));

        List<RendezVous> liste = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            liste.add(mapperResultat(rs));
        }

        rs.close();
        ps.close();
        return liste;
    }

    /**
     * Retourne tous les RDV (tous citoyens) pour un agent à une date donnée
     */
    public List<RendezVous> trouverParAgentEtDate(int agentId,
                                                   LocalDate date)
                                                   throws SQLException {
        String sql =
            "SELECT * FROM rendez_vous " +
            "WHERE agent_id = ? AND date_rdv = ? AND statut <> 'ANNULE' " +
            "ORDER BY heure_debut ASC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt( 1, agentId);
        ps.setDate(2, Date.valueOf(date));

        List<RendezVous> liste = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            liste.add(mapperResultat(rs));
        }

        rs.close();
        ps.close();
        return liste;
    }

    /**
     * Compte les RDV actifs pour un service à une date donnée
     */
    public int compterRdvActifs(int serviceId, LocalDate date) throws SQLException {
        String sql =
            "SELECT COUNT(*) FROM rendez_vous " +
            "WHERE service_id = ? AND date_rdv = ? AND statut <> 'ANNULE'";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt( 1, serviceId);
        ps.setDate(2, Date.valueOf(date));

        ResultSet rs = ps.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }

        rs.close();
        ps.close();
        return count;
    }

    // ============================================================
    //  UPDATE
    // ============================================================

    /**
     * Met à jour le statut d'un rendez-vous
     */
    public void mettreAJourStatut(int id, StatutRdv statut) throws SQLException {
        String sql =
            "UPDATE rendez_vous " +
            "SET statut = ?, date_modification = NOW() " +
            "WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, statut.name());
        ps.setInt(   2, id);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Affecte un agent à un rendez-vous
     */
    public void affecterAgent(int rdvId, int agentId) throws SQLException {
        String sql =
            "UPDATE rendez_vous " +
            "SET agent_id = ?, date_modification = NOW() " +
            "WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, agentId);
        ps.setInt(2, rdvId);
        ps.executeUpdate();
        ps.close();
    }

    // ============================================================
    //  DELETE (logique via annulation)
    // ============================================================

    /**
     * Annule un rendez-vous (suppression logique, non physique)
     */
    public void annuler(int id, String motifAnnulation) throws SQLException {
        String sql =
            "UPDATE rendez_vous " +
            "SET statut = 'ANNULE', observations = ?, date_modification = NOW() " +
            "WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "Annulé : " + motifAnnulation);
        ps.setInt(   2, id);
        ps.executeUpdate();
        ps.close();
    }

    // ============================================================
    //  MAPPING ResultSet → RendezVous
    // ============================================================

    /**
     * Construit un objet RendezVous à partir d'une ligne de ResultSet
     * Note : les objets Citoyen, Service, Agent ne sont pas chargés ici
     *        (utiliser des jointures ou des DAO dédiés si besoin)
     */
    private RendezVous mapperResultat(ResultSet rs) throws SQLException {
        RendezVous rdv = new RendezVous();
        rdv.setId(          rs.getInt("id"));
        rdv.setReference(   rs.getString("reference"));
        rdv.setDateRdv(     rs.getDate("date_rdv").toLocalDate());
        rdv.setHeureDebut(  rs.getString("heure_debut"));
        rdv.setHeureFin(    rs.getString("heure_fin"));
        rdv.setStatut(      StatutRdv.valueOf(rs.getString("statut")));
        rdv.setMotif(       rs.getString("motif"));
        rdv.setObservations(rs.getString("observations"));
        return rdv;
    }
}
