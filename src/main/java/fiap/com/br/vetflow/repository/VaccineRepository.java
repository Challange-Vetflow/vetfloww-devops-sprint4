package fiap.com.br.vetflow.repository;

import fiap.com.br.vetflow.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    List<Vaccine> findByPetId(Long petId);

    @Query("SELECT v FROM Vaccine v WHERE v.nextDoseAt < :today ORDER BY v.nextDoseAt")
    List<Vaccine> findExpired(@Param("today") LocalDate today);

    @Query("SELECT v FROM Vaccine v WHERE v.pet.id = :petId ORDER BY v.appliedAt DESC")
    List<Vaccine> findByPetIdOrderByAppliedAtDesc(@Param("petId") Long petId);

    @Query("SELECT v FROM Vaccine v WHERE v.nextDoseAt BETWEEN :from AND :to ORDER BY v.nextDoseAt")
    List<Vaccine> findDueBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
