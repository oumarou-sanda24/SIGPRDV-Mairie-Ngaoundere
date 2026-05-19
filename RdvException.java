package mairie.rdv.exception;

/**
 * classe faite par BONGA 24A429FS
 */
public class RdvException extends Exception {

    public RdvException(String message) {
        super(message);
    }

    public RdvException(String message, Throwable cause) {
        super(message, cause);
    }
}
