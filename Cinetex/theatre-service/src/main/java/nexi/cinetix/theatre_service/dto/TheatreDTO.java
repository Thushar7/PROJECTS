package nexi.cinetix.theatre_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import nexi.cinetix.theatre_service.entity.Theatre;

/**
 * Mutable DTO for Theatre (used for create/update + responses).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreDTO {
    private Long theatreId; // null for create

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    private String state;

    public static TheatreDTO fromEntity(Theatre entity) {
        if (entity == null) return null;
        return TheatreDTO.builder()
                .theatreId(entity.getTheatreId())
                .name(entity.getName())
                .city(entity.getCity())
                .state(entity.getState())
                .build();
    }

    public Theatre toEntity() {
        return Theatre.builder()
                .theatreId(theatreId)
                .name(name)
                .city(city)
                .state(state)
                .build();
    }
}
