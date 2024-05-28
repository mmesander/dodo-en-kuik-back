package nl.dodo_en_kuik.backend.controllers;

// Imports
import jakarta.validation.Valid;
import nl.dodo_en_kuik.backend.dtos.input.AuthorityInputDto;
import nl.dodo_en_kuik.backend.dtos.input.IdInputDto;
import nl.dodo_en_kuik.backend.dtos.input.UserInputDto;
import nl.dodo_en_kuik.backend.dtos.output.UserDto;
import nl.dodo_en_kuik.backend.exceptions.BadRequestException;
import nl.dodo_en_kuik.backend.exceptions.InvalidInputException;
import nl.dodo_en_kuik.backend.models.Authority;
import nl.dodo_en_kuik.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
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

    // ADMIN -- Movie Requests
    @PutMapping("/{username}/movies")
    public ResponseEntity<Object> assignMovieIdToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignMovieIdToUser(username, inputDto.getId());

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/movies")
    public ResponseEntity<Object> removeMovieIdFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeMovieIdFromUser(username, inputDto.getId());

            return ResponseEntity.ok().body(dto);
        }
    }

    // ADMIN -- Series Requests
    @PutMapping("/{username}/series")
    public ResponseEntity<Object> assignSeriesIdToUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.assignSeriesIdToUser(username, inputDto.getId());

            return ResponseEntity.ok().body(dto);
        }
    }

    @DeleteMapping("/{username}/series")
    public ResponseEntity<Object> removeSeriesIdFromUser(
            @PathVariable("username") String username,
            @Valid
            @RequestBody IdInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            UserDto dto = userService.removeSeriesIdFromUser(username, inputDto.getId());

            return ResponseEntity.ok().body(dto);
        }
    }
}
