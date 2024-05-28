package nl.dodo_en_kuik.backend.helpers;

// Imports
import nl.dodo_en_kuik.backend.interfaces.IdentifiableUsername;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

public class UriBuilder {
    public static URI buildUriWithUsername(IdentifiableUsername uriObject) {
        return URI.create((
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + uriObject.getUsername()).toUriString())
        );
    }
}