package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Tutor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TutorDtos {

    public record TutorRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 100)
            String name,

            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            String email,

            @NotBlank(message = "Telefone é obrigatório")
            @Size(min = 8, max = 20)
            String phone
    ) {
        public Tutor toEntity() {
            return Tutor.builder()
                    .name(name.trim())
                    .email(email.trim().toLowerCase())
                    .phone(phone.trim())
                    .build();
        }
    }

    public record TutorResponse(
            Long id,
            String name,
            String email,
            String phone,
            boolean active
    ) {
        public static TutorResponse fromEntity(Tutor t) {
            return new TutorResponse(t.getId(), t.getName(), t.getEmail(), t.getPhone(), t.isActive());
        }
    }
}
