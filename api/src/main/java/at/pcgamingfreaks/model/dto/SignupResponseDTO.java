package at.pcgamingfreaks.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDTO {
    boolean usernameTaken;
    boolean emailTaken;
    boolean signupSuccess;
}
