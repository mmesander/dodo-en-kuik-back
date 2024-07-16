package nl.dodo_en_kuik.backend.controllers;

// Imports

import jakarta.validation.Valid;
import nl.dodo_en_kuik.backend.dtos.input.AuthorityInputDto;
import nl.dodo_en_kuik.backend.dtos.input.IdInputDto;
import nl.dodo_en_kuik.backend.dtos.input.MultipleIdInputDto;
import nl.dodo_en_kuik.backend.dtos.input.UserInputDto;
import nl.dodo_en_kuik.backend.dtos.output.UserDto;
import nl.dodo_en_kuik.backend.exceptions.BadRequestException;
import nl.dodo_en_kuik.backend.exceptions.InvalidInputException;
import nl.dodo_en_kuik.backend.models.Authority;
import nl.dodo_en_kuik.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static nl.dodo_en_kuik.backend.helpers.BindingResultHelper.handleBindingResultError;
import static nl.dodo_en_kuik.backend.helpers.UriBuilder.buildUriWithUsername;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ALL -- Register
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(
            @Valid
            @RequestBody UserInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.createUser(inputDto);

            URI uri = buildUriWithUsername(dto);

            return ResponseEntity.created(uri).body(dto);
        }
    }

    // ADMIN -- CRUD Requests
    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> dtos = userService.getUsers();

        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("username") String username
    ) {
        UserDto dto = userService.getUser(username);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        List<UserDto> dtos = userService.getUsersByFilter(username, email);

        return ResponseEntity.ok().body(dtos);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("username") String username
    ) {
        String confirmation = userService.deleteUser(username);

        return ResponseEntity.ok().body(confirmation);
    }

    // ADMIN -- Authority Requests
    @GetMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> getUserAuthorities(
            @PathVariable("username") String username
    ) {
        Set<Authority> authorities = userService.getUserAuthorities(username);

        return ResponseEntity.ok().body(authorities);
    }

    @PutMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> assignAuthorityToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody AuthorityInputDto authorityInputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            try {
                UserDto dto = userService.assignAuthorityToUser(username, authorityInputDto.getAuthority().toUpperCase());

                return ResponseEntity.ok().body(dto);
            } catch (Exception exception) {
                throw new BadRequestException(exception.getMessage());
            }
        }
    }

    @DeleteMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> removeAuthorityFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody AuthorityInputDto authorityInputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            String confirmation = userService.removeAuthorityFromUser(username, authorityInputDto.getAuthority());

            return ResponseEntity.ok().body(confirmation);
        }
    }

    // ADMIN -- Single Movie Requests
    @PutMapping("/{username}/movies/favorites")
    public ResponseEntity<Object> assignFavoriteMovieToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "favorites", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/movies/watchlist")
    public ResponseEntity<Object> assignWatchlistMovieToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watchlist", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/movies/watched")
    public ResponseEntity<Object> assignWatchedMovieToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watched", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/favorites")
    public ResponseEntity<Object> removeFavoriteMovieFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "favorites", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/watchlist")
    public ResponseEntity<Object> removeWatchlistMovieFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watchlist", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/watched")
    public ResponseEntity<Object> removeWatchedMovieFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watched", true);

            return ResponseEntity.ok().body(dto);
        }
    }

    // ADMIN -- Single Series Requests
    @PutMapping("/{username}/series/favorites")
    public ResponseEntity<Object> assignFavoriteSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "favorites", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/series/watchlist")
    public ResponseEntity<Object> assignWatchlistSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watchlist", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/series/watched")
    public ResponseEntity<Object> assignWatchedSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watched", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/favorites")
    public ResponseEntity<Object> removeFavoriteSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "favorites", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/watchlist")
    public ResponseEntity<Object> removeWatchlistSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watchlist", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/watched")
    public ResponseEntity<Object> removeWatchedSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watched", false);

            return ResponseEntity.ok().body(dto);
        }
    }

    // ADMIN -- Multiple Movie Requests
    @PutMapping("/{username}/movies/favorites-list")
    public ResponseEntity<Object> assignMultipleFavoriteMoviesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "favorites", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/movies/watchlist-list")
    public ResponseEntity<Object> assignMultipleWatchlistMoviesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "watchlist", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/movies/watched-list")
    public ResponseEntity<Object> assignMultipleWatchedMoviesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "watched", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/favorites-list")
    public ResponseEntity<Object> removeMultipleFavoriteMoviesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "favorites", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/watchlist-list")
    public ResponseEntity<Object> removeMultipleWatchlistMoviesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "watchlist", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies/watched-list")
    public ResponseEntity<Object> removeMultipleWatchedMoviesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "watched", true
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    // ADMIN -- Multiple Series Requests
    @PutMapping("/{username}/series/favorites-list")
    public ResponseEntity<Object> assignMultipleFavoriteSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "favorites", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/series/watchlist-list")
    public ResponseEntity<Object> assignMultipleWatchlistSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "watchlist", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @PutMapping("/{username}/series/watched-list")
    public ResponseEntity<Object> assignMultipleWatchedSeriesToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMultipleIdsToSpecificUserList(
                    username, inputDto.getIds(), "watched", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/favorites-list")
    public ResponseEntity<Object> removeMultipleFavoriteSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "favorites", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/watchlist-list")
    public ResponseEntity<Object> removeMultipleWatchlistSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "watchlist", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series/watched-list")
    public ResponseEntity<Object> removeMultipleWatchedSeriesFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody MultipleIdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMultipleIdsFromSpecificUserList(
                    username, inputDto.getIds(), "watched", false
            );

            return ResponseEntity.ok().body(dto);
        }
    }

    // USER (AUTH) -- CRUD Requests
    @GetMapping(value = "/auth/{username}")
    public ResponseEntity<UserDto> getAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            UserDto dto = userService.getUser(username);

            return ResponseEntity.ok().body(dto);
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    // USER (AUTH) -- Movies Requests
    @PutMapping("/auth/{username}/movies/favorites")
    public ResponseEntity<Object> assignFavoriteMovieToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "favorites", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @PutMapping("/auth/{username}/movies/watchlist")
    public ResponseEntity<Object> assignWatchlistMovieToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watchlist", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @PutMapping("/auth/{username}/movies/watched")
    public ResponseEntity<Object> assignWatchedMovieToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watched", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/movies/favorites")
    public ResponseEntity<Object> removeFavoriteMovieFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "favorites", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/movies/watchlist")
    public ResponseEntity<Object> removeWatchlistMovieFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watchlist", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/movies/watched")
    public ResponseEntity<Object> removeWatchedMovieFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watched", true);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    // USER (AUTH) -- Series Requests
    @PutMapping("/auth/{username}/series/favorites")
    public ResponseEntity<Object> assignFavoriteSeriesToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "favorites", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @PutMapping("/auth/{username}/series/watchlist")
    public ResponseEntity<Object> assignWatchlistSeriesToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watchlist", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @PutMapping("/auth/{username}/series/watched")
    public ResponseEntity<Object> assignWatchedSeriesToAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.assignIdToSpecificUserList(username, inputDto.getId(), "watched", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/series/favorites")
    public ResponseEntity<Object> removeFavoriteSeriesFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "favorites", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/series/watchlist")
    public ResponseEntity<Object> removeWatchlistSeriesFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watchlist", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }

    @DeleteMapping("/auth/{username}/series/watched")
    public ResponseEntity<Object> removeWatchedSeriesFromAuthUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (Objects.equals(userDetails.getUsername(), username.toUpperCase())) {
            if (bindingResult.hasFieldErrors()) {
                throw new InvalidInputException(handleBindingResultError(bindingResult));
            } else {
                UserDto dto = userService.removeIdFromSpecificUserList(username, inputDto.getId(), "watched", false);

                return ResponseEntity.ok().body(dto);
            }
        } else {
            throw new BadRequestException("Used token is not valid");
        }
    }
}
