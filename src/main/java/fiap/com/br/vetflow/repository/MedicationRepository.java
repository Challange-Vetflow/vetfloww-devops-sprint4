package fiap.com.br.vetflow.repository;

import fiap.com.br.vetflow.entity.Medication;
import fiap.com.br.vetflow.entity.MedicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByPetId(Long petId);
    List<Medication> findByStatus(MedicationStatus status);

    @Query("SELECT m FROM Medication m WHERE m.status = 'ACTIVE' ORDER BY m.startDate")
    List<Medication> findAllActive();

    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND m.status = :status ORDER BY m.startDate DESC")
    List<Medication> findByPetIdAndStatus(@Param("petId") Long petId, @Param("status") MedicationStatus status);
}
