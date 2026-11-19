package app.samflix.macros.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import app.samflix.macros.common.ApiExceptionHandler;
import app.samflix.macros.config.SecurityConfig;


@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class})
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	UserService userService;

	private static final String VALID_BODY = """
			{"email":"test@example.com","name":"sam","password":"supersecret1"}
			""";

	@Test
	void returns201ForValidRegistration() throws Exception {
		when(userService.register(anyString(), anyString(), anyString()))
			.thenReturn(new User("test@example.com", "test", "supersecret1"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_BODY))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.name").value("test"))
				.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	void returns400AndFieldErrorsForInvalidInput() throws Exception {
		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"nope","name":"","password":"short"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.email").exists())
				.andExpect(jsonPath("$.errors.name").exists())
				.andExpect(jsonPath("$.errors.password").exists());
	}

	@Test
	void returns409WhenEmailAlreadyUsed() throws Exception {
		when(userService.register(any(), any(), any()))
			.thenThrow(new EmailAlreadyUsedException());

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_BODY))
				.andExpect(status().isConflict());
	}
}
