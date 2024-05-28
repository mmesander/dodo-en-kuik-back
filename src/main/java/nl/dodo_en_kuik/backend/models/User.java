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
@Table(name = "users")
public class User {
    // Variables
    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // Relations
    @OneToMany(
            targetEntity = Authority.class,
            mappedBy = "username",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<Authority> authorities = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_movie_ids",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "movie_id")
    private Set<Long> movieIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_series_ids",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "series_id")
    private Set<Long> seriesIds = new HashSet<>();

    // Methods
    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }

    public void removeAuthority(Authority authority) {
        this.authorities.remove(authority);
    }

    public void addMovieId(Long movieId) {
        this.movieIds.add(movieId);
    }

    public void removeMovieId(Long movieId) {
        this.movieIds.remove(movieId);
    }

    public void addSeriesId(Long seriesId) {
        this.seriesIds.add(seriesId);
    }

    public void removeSeriesId(Long seriesId) {
        this.seriesIds.remove(seriesId);
    }
}
