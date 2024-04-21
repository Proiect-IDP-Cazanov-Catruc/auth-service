/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "First name should be provided")
	private String firstName;

	@NotBlank(message = "Last name should be provided")
	private String lastName;

	@NotBlank(message = "Email should be provided")
	@Email(message = "Provided string is not email")
	private String email;

	@NotBlank(message = "Password should be provided")
	private String password;
}
