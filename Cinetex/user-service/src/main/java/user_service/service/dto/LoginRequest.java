package user_service.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username; // can be extended to accept email too

    @NotBlank
    private String password;
}
