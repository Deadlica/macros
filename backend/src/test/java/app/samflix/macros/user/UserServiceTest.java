package app.samflix.macros.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	UserService userService;

	@Test
	void registersUserWithNormalizedEmailAndHashedPassword() {
		when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
		when(passwordEncoder.encode("supersecret1")).thenReturn("hashed-value");
		when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		userService.register("  Test@Example.com", "test", "supersecret1");

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).saveAndFlush(captor.capture());
		User saved = captor.getValue();

		assertThat(saved.getEmail()).isEqualTo("test@example.com");
		assertThat(saved.getPasswordHash()).isEqualTo("hashed-value");
		assertThat(saved.getPasswordHash()).isNotEqualTo("supersecret1");
	}

	@Test
	void rejectsDuplicateEmail() {
		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.register("test@example.com", "test", "supersecret1"))
			.isInstanceOf(EmailAlreadyUsedException.class);
		verify(userRepository, never()).saveAndFlush(any());
	}

	@Test
	void translateDatabaseUniqueViolationInfoEmailAlreadyUsed() {
		when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
		when(passwordEncoder.encode("supersecret1")).thenReturn("hashed-value");
		when(userRepository.saveAndFlush(any(User.class)))
			.thenThrow(new DataIntegrityViolationException("duplicate"));

		assertThatThrownBy(() -> userService.register("test@example.com", "test", "supersecret1"))
			.isInstanceOf(EmailAlreadyUsedException.class);
	}
}
