package at.pcgamingfreaks.service;

import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.*;
import at.pcgamingfreaks.model.repo.*;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.steam.SteamEntryScore;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	private final AniListEntryScoreRepository aniListEntryScoreRepository;
	private final TraktEntryScoreRepository traktEntryScoreRepository;
	private final SteamEntryScoreRepository steamEntryScoreRepository;

	public LoginResponseDTO authenticate(String username, String password) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		User user = (User) auth.getPrincipal();
		String token = jwtService.create(user.getUsername());
		return new LoginResponseDTO(token);
	}

	@Transactional
	public SignupResponseDTO signup(SignupRequestDTO request) {
		SignupResponseDTO response = new SignupResponseDTO();
		response.setUsernameTaken(userRepository.findByUsername(request.getUsername()).isPresent());
		response.setEmailTaken(userRepository.findByEmail(request.getEmail()).isPresent());

		if (!(response.isUsernameTaken() || response.isEmailTaken())) {
			User user = new User();
			user.setUsername(request.getUsername());
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setCreatedAt(LocalDateTime.now());
			user.setUpdatedAt(LocalDateTime.now());
			userRepository.save(user);
			response.setSignupSuccess(true);
		}

		return response;
	}

	public LoginResponseDTO refreshToken(String token) {
		if (jwtService.isTokenExpired(token)) throw new CredentialsExpiredException("Token expired");

		String username = jwtService.extractUsername(token);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		String refreshedToken = jwtService.create(user.getUsername());

		return new LoginResponseDTO(refreshedToken);
	}

	@Transactional
	public void changePassword(ChangePasswordRequestDTO request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException(request.getUsername()));

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getOldPassword()));
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Transactional
	public void deleteAccount(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		aniListEntryScoreRepository.deleteAllByUser(user);
		traktEntryScoreRepository.deleteAllByUser(user);
		steamEntryScoreRepository.deleteAllByUser(user);
		userRepository.delete(user);
		log.info("Deleted {} successfully", user.getUsername());
	}

}
