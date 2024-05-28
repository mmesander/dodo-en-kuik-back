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
@Table(name = "serieIds")
public class SeriesId {
    @Id
    private Long id;

    // Relations
    @ManyToMany
    @JoinTable(
            name = "seriesId_users",
            joinColumns = @JoinColumn(name = "series_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private Set<User> users = new HashSet<>();
}
