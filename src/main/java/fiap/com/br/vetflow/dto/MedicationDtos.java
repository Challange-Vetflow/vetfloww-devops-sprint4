package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Medication;
import fiap.com.br.vetflow.entity.MedicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MedicationDtos {

    public record MedicationRequest(
            @NotNull(message = "Id do pet é obrigatório")
            Long petId,

            @NotBlank(message = "Nome do medicamento é obrigatório")
            @Size(min = 2, max = 100)
            String name,

            @NotBlank(message = "Dosagem é obrigatória")
            String dosage,

            @NotBlank(message = "Frequência é obrigatória")
            String frequency,

            @NotNull(message = "Data de início é obrigatória")
            LocalDate startDate,

            @NotNull(message = "Data de fim é obrigatória")
            LocalDate endDate
    ) {}

    public record MedicationResponse(
            Long id,
            Long petId,
            String name,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            MedicationStatus status
    ) {
        public static MedicationResponse fromEntity(Medication m) {
            return new MedicationResponse(
                    m.getId(),
                    m.getPet() != null ? m.getPet().getId() : null,
                    m.getName(),
                    m.getDosage(),
                    m.getFrequency(),
                    m.getStartDate(),
                    m.getEndDate(),
                    m.getStatus()
            );
        }
    }
}
