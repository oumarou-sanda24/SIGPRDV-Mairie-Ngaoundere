package mairie.rdv.exception;

/**
 * Exception métier personnalisée pour le système de RDV
 * Levée lorsqu'une règle métier est violée
 * Mairie de Ngaoundéré - SIGPRDV
 */
public class RdvException extends Exception {

    public RdvException(String message) {
        super(message);
    }

    public RdvException(String message, Throwable cause) {
        super(message, cause);
    }
}
