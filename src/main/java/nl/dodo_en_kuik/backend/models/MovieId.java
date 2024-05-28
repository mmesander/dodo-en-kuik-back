package nl.dodo_en_kuik.backend.models;

// Imports
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "movieIds")
public class MovieId {
    @Id
    private Long id;

    // Relations
    @ManyToMany
    @JoinTable(
            name = "movieId_users",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private Set<User> users = new HashSet<>();
}
