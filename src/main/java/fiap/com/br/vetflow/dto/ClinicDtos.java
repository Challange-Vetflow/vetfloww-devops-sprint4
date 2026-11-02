package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Clinic;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClinicDtos {

    public record ClinicRequest(
            @NotBlank(message = "Nome da clínica é obrigatório")
            @Size(min = 2, max = 200)
            String name,

            String address,

            String phone,

            @Email(message = "E-mail inválido")
            String email
    ) {}

    public record ClinicResponse(
            Long id,
            String name,
            String address,
            String phone,
            String email,
            boolean active
    ) {
        public static ClinicResponse fromEntity(Clinic c) {
            return new ClinicResponse(c.getId(), c.getName(), c.getAddress(), c.getPhone(), c.getEmail(), c.isActive());
        }
    }
}
