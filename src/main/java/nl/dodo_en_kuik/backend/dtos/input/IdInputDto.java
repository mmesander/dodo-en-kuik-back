package nl.dodo_en_kuik.backend.dtos.input;

// Imports
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdInputDto {
    @NotNull(message = "Id is required")
    private Long id;
}
