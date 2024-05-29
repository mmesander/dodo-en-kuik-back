package nl.dodo_en_kuik.backend.services;

// Imports

import nl.dodo_en_kuik.backend.dtos.input.UserInputDto;
import nl.dodo_en_kuik.backend.dtos.output.UserDto;
import nl.dodo_en_kuik.backend.exceptions.BadRequestException;
import nl.dodo_en_kuik.backend.exceptions.InvalidInputException;
import nl.dodo_en_kuik.backend.exceptions.RecordNotFoundException;
import nl.dodo_en_kuik.backend.exceptions.UsernameNotFoundException;
import nl.dodo_en_kuik.backend.models.Authority;
import nl.dodo_en_kuik.backend.models.User;
import nl.dodo_en_kuik.backend.repositories.AuthorityRepository;
import nl.dodo_en_kuik.backend.repositories.UserRepository;
import nl.dodo_en_kuik.backend.specifications.UserSpecification;
import org.springframework.stereotype.Service;

import java.util.*;

import static nl.dodo_en_kuik.backend.helpers.CopyProperties.copyProperties;
import static nl.dodo_en_kuik.backend.security.config.SpringSecurityConfig.passwordEncoder;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(
            UserRepository userRepository,
            AuthorityRepository authorityRepository
    ) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    // Transfer Methods
    public User dtoToUser(UserInputDto inputDto) {
        User user = new User();

        user.setUsername(inputDto.getUsername().toUpperCase());
        user.setPassword(passwordEncoder().encode(inputDto.getPassword()));
        user.setEmail(inputDto.getEmail());

        return user;
    }

    public UserDto userToDto(User user) {
        UserDto userDto = new UserDto();

        copyProperties(user, userDto);

        return userDto;
    }

    // CRUD Methods
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            UserDto userDto = userToDto(user);
            userDtos.add(userDto);
        }

        if (userDtos.isEmpty()) {
            throw new RecordNotFoundException("No users found");
        } else {
            userDtos.sort(Comparator.comparing(UserDto::getUsername));
            return userDtos;
        }
    }

    public UserDto getUser(String username) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        return userToDto(user);
    }

    public List<UserDto> getUsersByFilter(
            String username,
            String email
    ) {
        UserSpecification filters = new UserSpecification(username, email);

        List<User> filteredUsers = userRepository.findAll(filters);
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : filteredUsers) {
            UserDto userDto = userToDto(user);
            userDtos.add(userDto);
        }

        if (userDtos.isEmpty()) {
            throw new RecordNotFoundException("No users found with the specified filters");
        } else {
            userDtos.sort(Comparator.comparing(UserDto::getUsername));
            return userDtos;
        }
    }

    public UserDto createUser(UserInputDto inputDto) {
        User user = dtoToUser(inputDto);

        boolean usernameExists = userRepository.existsByUsernameIgnoreCase(inputDto.getUsername());
        boolean emailExists = userRepository.existsByEmailIgnoreCase(inputDto.getEmail());

        if (usernameExists && emailExists) {
            throw new InvalidInputException("Gebruikersnaam en emailadres zijn al in gebruik");
        } else if (usernameExists) {
            throw new InvalidInputException("Gebruikersnaam is al in gebruik");
        } else if (emailExists) {
            throw new InvalidInputException("Email adres is al in gebruik");
        } else {
            user.addAuthority(new Authority(user.getUsername(), "ROLE_USER"));
            userRepository.save(user);

            return userToDto(user);
        }
    }

    public String deleteUser(String username) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getUsername().equalsIgnoreCase("mmesander")) {
            throw new BadRequestException("Can't remove user: " + user.getUsername().toUpperCase());
        }

        userRepository.deleteById(usernameUppercase);

        return "User: " + usernameUppercase + " is deleted";
    }

    // Relation - Authorities Methods
    public Set<Authority> getUserAuthorities(String username) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        UserDto userDto = userToDto(user);

        return userDto.getAuthorities();
    }

    public UserDto assignAuthorityToUser(String username, String authority) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        Optional<Authority> optionalAuthority = authorityRepository.findAuthoritiesByAuthorityContainsIgnoreCase(authority);
        UserDto userDto;

        if (user != null && optionalAuthority.isPresent()) {
            user.addAuthority(new Authority(usernameUppercase, authority));

            userRepository.save(user);

            userDto = userToDto(user);
        } else {
            throw new BadRequestException("Authority: " + authority.toUpperCase() + " not found");
        }

        return userDto;
    }

    public String removeAuthorityFromUser(String username, String authority) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        Authority toRemove = user.getAuthorities().stream()
                .filter(a -> a.getAuthority().equalsIgnoreCase(authority))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("user: " + usernameUppercase + " does not have authority "
                        + authority.toUpperCase()));

        long count = 0;

        if (toRemove.getUsername() != null) {
            List<User> users = userRepository.findAll();

            if (!users.isEmpty()) {
                for (User user1 : users) {
                    for (Authority checkAuthority : user1.getAuthorities()) {
                        if (checkAuthority.getAuthority().equalsIgnoreCase(authority)) {
                            count++;
                            break;
                        }
                    }
                }
            }
        }

        if (count <= 1) {
            throw new BadRequestException("At least 1 user must have the authority: " + authority.toUpperCase());
        } else {
            user.removeAuthority(toRemove);
            userRepository.save(user);

            return "Authority " + authority.toUpperCase() + " is removed from user: " + usernameUppercase;
        }
    }

    // Relation - Movies Methods
    public UserDto assignFavoriteMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getFavoriteMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is al toegevoegd aan favorieten");
        } else {
            user.addFavoriteMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto assignWatchlistMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchlistMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is al toegevoegd aan watchlist");
        } else {
            user.addWatchlistMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto assignWatchedMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchedMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is al toegevoegd aan al gezien");
        } else {
            user.addWatchedMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeFavoriteMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getFavoriteMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is niet toegevoegd aan favorieten");
        } else {
            user.removeFavoriteMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeWatchlistMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchlistMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is niet toegevoegd aan watchlist");
        } else {
            user.removeWatchlistMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeWatchedMovieToUser(String username, Long movieId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchedMovies().contains(movieId)) {
            throw new BadRequestException("Film: " + movieId + " is niet toegevoegd aan al gezien");
        } else {
            user.removeWatchedMovie(movieId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    // Relation - Series Methods
    public UserDto assignFavoriteSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getFavoriteSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is al toegevoegd aan favorieten");
        } else {
            user.addFavoriteSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto assignWatchlistSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchlistSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is al toegevoegd aan watchlist");
        } else {
            user.addWatchlistSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto assignWatchedSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchedSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is al toegevoegd aan al gezien");
        } else {
            user.addWatchedSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeFavoriteSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getFavoriteSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is niet toegevoegd aan favorieten");
        } else {
            user.removeFavoriteSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeWatchlistSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchlistSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is niet toegevoegd aan watchlist");
        } else {
            user.removeWatchlistSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }

    public UserDto removeWatchedSeriesToUser(String username, Long seriesId) {
        String usernameUppercase = username.toUpperCase();

        User user = userRepository.findById(usernameUppercase)
                .orElseThrow(() -> new UsernameNotFoundException(usernameUppercase));

        if (user.getWatchedSeries().contains(seriesId)) {
            throw new BadRequestException("Serie: " + seriesId + " is niet toegevoegd aan al gezien");
        } else {
            user.removeWatchedSeries(seriesId);
            userRepository.save(user);
        }

        return userToDto(user);
    }
}
