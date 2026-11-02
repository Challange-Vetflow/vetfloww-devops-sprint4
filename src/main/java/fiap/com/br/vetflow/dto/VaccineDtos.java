package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Vaccine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class VaccineDtos {

    public record VaccineRequest(
            @NotNull(message = "Id do pet é obrigatório")
            Long petId,

            @NotBlank(message = "Nome da vacina é obrigatório")
            @Size(min = 2, max = 100)
            String vaccineName,

            @NotNull(message = "Data de aplicação é obrigatória")
            LocalDate appliedAt,

            @NotNull(message = "Data da próxima dose é obrigatória")
            LocalDate nextDoseAt,

            String batch
    ) {}

    public record VaccineResponse(
            Long id,
            Long petId,
            String vaccineName,
            LocalDate appliedAt,
            LocalDate nextDoseAt,
            String batch,
            boolean expired
    ) {
        public static VaccineResponse fromEntity(Vaccine v) {
            return new VaccineResponse(
                    v.getId(),
                    v.getPet() != null ? v.getPet().getId() : null,
                    v.getVaccineName(),
                    v.getAppliedAt(),
                    v.getNextDoseAt(),
                    v.getBatch(),
                    v.isExpired()
            );
        }
    }
}
