package fiap.com.br.vetflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv_vaccines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pet é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pet pet;

    @NotBlank(message = "Nome da vacina é obrigatório")
    @Size(min = 2, max = 100)
    @Column(name = "vaccine_name", nullable = false, length = 100)
    private String vaccineName;

    @NotNull(message = "Data de aplicação é obrigatória")
    @Column(name = "applied_at", nullable = false)
    private LocalDate appliedAt;

    @NotNull(message = "Data da próxima dose é obrigatória")
    @Column(name = "next_dose_at", nullable = false)
    private LocalDate nextDoseAt;

    @Column(length = 50)
    private String batch;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return nextDoseAt != null && nextDoseAt.isBefore(LocalDate.now());
    }
}
