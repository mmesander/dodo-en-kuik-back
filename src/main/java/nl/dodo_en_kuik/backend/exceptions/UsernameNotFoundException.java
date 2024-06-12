package nl.dodo_en_kuik.backend.exceptions;

// Imports
import java.io.Serial;

public class UsernameNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UsernameNotFoundException() {
        super();
    }

    public UsernameNotFoundException(String username) {
        super("Gebruiker: " + username + " niet gevonden");
    }
}
