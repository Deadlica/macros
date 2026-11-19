package app.samflix.macros.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
	@NotBlank @Email @Size(max = 254) String email,
	@NotBlank @Size(min = 1, max = 72) String name,
	@NotBlank @Size(min = 8, max = 72) String password
) {}
