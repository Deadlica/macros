package app.samflix.macros.user;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User register(String email, String name, String rawPassword) {
		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new EmailAlreadyUsedException();
		}

		User user = new User(normalizedEmail, name, passwordEncoder.encode(rawPassword));
		try {
			return userRepository.saveAndFlush(user);
		} catch (DataIntegrityViolationException e) {
			throw new EmailAlreadyUsedException();
		}
	}
}
