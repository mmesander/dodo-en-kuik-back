package nl.dodo_en_kuik.backend.specifications;

// Imports
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nl.dodo_en_kuik.backend.models.User;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification implements Specification<User> {
    private String username;
    private String email;

    public UserSpecification(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            username = username.toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username + "%"));
        }

        if (email != null && !email.isEmpty()) {
            email = email.toLowerCase();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}