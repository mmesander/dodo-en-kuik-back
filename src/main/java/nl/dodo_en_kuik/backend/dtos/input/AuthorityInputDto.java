package nl.dodo_en_kuik.backend.dtos.input;

// Imports
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityInputDto {
    @NotNull(message = "Authority is required")
    @Pattern(regexp = "^ROLE_[a-zA-Z]+$", message = "Authority must start with ROLE_ and contain only letters")
    private String authority;
}
