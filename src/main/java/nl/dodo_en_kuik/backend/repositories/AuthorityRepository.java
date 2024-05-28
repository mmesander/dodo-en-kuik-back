package nl.dodo_en_kuik.backend.repositories;

// Imports
import nl.dodo_en_kuik.backend.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Optional<Authority> findAuthoritiesByAuthorityContainsIgnoreCaseAndUsernameIgnoreCase(String username, String authority);
}