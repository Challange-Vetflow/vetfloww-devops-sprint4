package fiap.com.br.vetflow.dto;

import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.entity.Species;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PetDtos {

    public record PetRequest(
            @NotBlank(message = "Nome do pet é obrigatório")
            @Size(min = 1, max = 100)
            String name,

            @NotNull(message = "Espécie é obrigatória")
            Species species,

            String breed,

            @NotNull(message = "Data de nascimento é obrigatória")
            LocalDate birthDate,

            @DecimalMin(value = "0.01", message = "Peso deve ser maior que zero")
            Double weightKg,

            @NotNull(message = "Id do tutor é obrigatório")
            Long tutorId
    ) {}

    public record PetResponse(
            Long id,
            String name,
            Species species,
            String breed,
            LocalDate birthDate,
            Double weightKg,
            int ageInMonths,
            Long tutorId,
            boolean active
    ) {
        public static PetResponse fromEntity(Pet p) {
            return new PetResponse(
                    p.getId(), p.getName(), p.getSpecies(), p.getBreed(),
                    p.getBirthDate(), p.getWeightKg(), p.getAgeInMonths(),
                    p.getTutor() != null ? p.getTutor().getId() : null,
                    p.isActive()
            );
        }
    }
}
