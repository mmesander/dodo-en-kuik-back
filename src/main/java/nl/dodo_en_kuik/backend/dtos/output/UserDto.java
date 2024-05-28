package nl.dodo_en_kuik.backend.dtos.output;

// Imports
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import nl.dodo_en_kuik.backend.interfaces.IdentifiableUsername;
import nl.dodo_en_kuik.backend.models.Authority;
import java.util.Set;

@Getter
@Setter
public class UserDto implements IdentifiableUsername {
    // Variables
    private String username;
    private String password;
    private String email;

    // Relations
    @JsonSerialize
    private Set<Authority> authorities;
    private Set<Long> movieIds;
    private Set<Long> seriesIds;
}
