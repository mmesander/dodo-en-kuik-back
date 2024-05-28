package nl.dodo_en_kuik.backend.dtos.input;

// Imports
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputDto {
    @NotNull(message = "username is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "Username should contain only letters")
    private String username;

    @NotNull
    @Size(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    @Pattern.List({
            @Pattern(regexp = ".*\\d.*", message = "Password must contain a digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain a lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain an uppercase letter"),
            @Pattern(regexp = ".*[@#$%^&+=!?].*", message = "Password must contain a special character")
    })
    private String password;

    @NotNull(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;
}
