package app.samflix.macros.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import app.samflix.macros.TestcontainersConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = "DB_PASSWORD=unused")
@Import(TestcontainersConfiguration.class)
@Transactional
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void savesAndReadsBackUser() {
		String exampleEmail = "test@example.com";
		String exampleName = "test";
		String examplePassword = "hashedPassword";
		User saved = userRepository.save(new User(exampleEmail, exampleName, examplePassword));
		entityManager.flush();
		entityManager.clear();

		User found = userRepository.findById(saved.getId()).orElseThrow();

		assertThat(found.getEmail()).isEqualTo(exampleEmail);
		assertThat(found.getName()).isEqualTo(exampleName);
		assertThat(found.getPasswordHash()).isEqualTo(examplePassword);
		assertThat(found.getCreatedAt()).isNotNull();
	}

	@Test
	void findsUserByEmail() {
		String exampleEmail = "test@example.com";
		String exampleName = "test";
		String examplePassword = "hashedPassword";
		userRepository.save(new User(exampleEmail, exampleName, examplePassword));

		assertThat(userRepository.findByEmail(exampleEmail)).isPresent();
		assertThat(userRepository.existsByEmail("fake@mail.com")).isFalse();
	}

	@Test
	void rejectsDuplicateEmail() {
		userRepository.saveAndFlush(new User("dup@example.com", "dup", "hash"));

		assertThatThrownBy(() ->
			userRepository.saveAndFlush(new User("dup@example.com", "dup2", "hash")))
			.isInstanceOf(DataIntegrityViolationException.class);
	}
}
