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
            name = "user_favorite_movies",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "movie_id")
    private Set<Long> favoriteMovies = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_watchlist_movies",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "movie_id")
    private Set<Long> watchlistMovies = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_watched_movies",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "movie_id")
    private Set<Long> watchedMovies = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_favorite_series",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "series_id")
    private Set<Long> favoriteSeries = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_watchlist_series",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "series_id")
    private Set<Long> watchlistSeries = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_watched_series",
            joinColumns = @JoinColumn(name = "username")
    )
    @Column(name = "series_id")
    private Set<Long> watchedSeries = new HashSet<>();

    // Authority Methods
    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }

    public void removeAuthority(Authority authority) {
        this.authorities.remove(authority);
    }

    // Movie Methods
    public void addFavoriteMovie(Long favoriteMovie) {
        this.favoriteMovies.add(favoriteMovie);
    }

    public void addWatchlistMovie(Long watchlistMovie) {
        this.watchlistMovies.add(watchlistMovie);
    }

    public void addWatchedMovie(Long watchedMovie) {
        this.watchedMovies.add(watchedMovie);
    }

    public void removeFavoriteMovie(Long favoriteMovie) {
        this.favoriteMovies.remove(favoriteMovie);
    }

    public void removeWatchlistMovie(Long watchlistMovie) {
        this.watchlistMovies.remove(watchlistMovie);
    }

    public void removeWatchedMovie(Long watchedMovie) {
        this.watchedMovies.remove(watchedMovie);
    }

    // Series Methods
    public void addFavoriteSeries(Long favoriteSeries) {
        this.favoriteSeries.add(favoriteSeries);
    }

    public void addWatchlistSeries(Long watchlistSeries) {
        this.watchlistSeries.add(watchlistSeries);
    }

    public void addWatchedSeries(Long watchedSeries) {
        this.watchedSeries.add(watchedSeries);
    }

    public void removeFavoriteSeries(Long favoriteSeries) {
        this.favoriteSeries.remove(favoriteSeries);
    }

    public void removeWatchlistSeries(Long watchlistSeries) {
        this.watchlistSeries.remove(watchlistSeries);
    }

    public void removeWatchedSeries(Long watchedSeries) {
        this.watchedSeries.remove(watchedSeries);
    }
}
