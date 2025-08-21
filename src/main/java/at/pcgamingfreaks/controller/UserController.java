package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.mapper.UserDtoMapper;
import at.pcgamingfreaks.model.dto.UserDTO;
import at.pcgamingfreaks.model.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("{username}")
    public ResponseEntity<UserDTO> user(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(value -> ResponseEntity.ok(UserDtoMapper.map(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
