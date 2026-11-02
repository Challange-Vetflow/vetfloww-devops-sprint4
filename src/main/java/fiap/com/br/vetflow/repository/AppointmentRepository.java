package fiap.com.br.vetflow.repository;

import fiap.com.br.vetflow.entity.Appointment;
import fiap.com.br.vetflow.entity.AppointmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPetId(Long petId);
    List<Appointment> findByClinicId(Long clinicId);
    Page<Appointment> findAll(Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.completed = false AND a.scheduledAt >= :now ORDER BY a.scheduledAt")
    List<Appointment> findPending(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Appointment a WHERE a.pet.id = :petId AND a.type = :type ORDER BY a.scheduledAt DESC")
    List<Appointment> findByPetIdAndType(@Param("petId") Long petId, @Param("type") AppointmentType type);

    @Query("SELECT a FROM Appointment a WHERE a.clinic.id = :clinicId AND a.completed = false ORDER BY a.scheduledAt")
    List<Appointment> findPendingByClinic(@Param("clinicId") Long clinicId);
}
