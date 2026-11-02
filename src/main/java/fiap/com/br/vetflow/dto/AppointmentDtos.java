package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Appointment;
import fiap.com.br.vetflow.entity.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentDtos {

    public record AppointmentRequest(
            @NotNull(message = "Id do pet é obrigatório")
            Long petId,

            @NotNull(message = "Id da clínica é obrigatório")
            Long clinicId,

            @NotNull(message = "Data do agendamento é obrigatória")
            LocalDateTime scheduledAt,

            @NotNull(message = "Tipo de consulta é obrigatório")
            AppointmentType type,

            String notes
    ) {}

    public record AppointmentResponse(
            Long id,
            Long petId,
            Long clinicId,
            LocalDateTime scheduledAt,
            AppointmentType type,
            String notes,
            boolean completed
    ) {
        public static AppointmentResponse fromEntity(Appointment a) {
            return new AppointmentResponse(
                    a.getId(),
                    a.getPet() != null ? a.getPet().getId() : null,
                    a.getClinic() != null ? a.getClinic().getId() : null,
                    a.getScheduledAt(),
                    a.getType(),
                    a.getNotes(),
                    a.isCompleted()
            );
        }
    }
}
