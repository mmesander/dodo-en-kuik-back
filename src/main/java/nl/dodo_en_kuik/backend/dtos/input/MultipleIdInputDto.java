package nl.dodo_en_kuik.backend.dtos.input;

// Imports
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MultipleIdInputDto {
    @NotNull(message = "Ids are required")
    private List<Long> ids;
}
