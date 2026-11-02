package fiap.com.br.vetflow.repository;

import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.entity.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByTutorId(Long tutorId);
    List<Pet> findBySpecies(Species species);
    Page<Pet> findAll(Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.tutor.id = :tutorId ORDER BY p.name")
    List<Pet> findByTutorIdOrderByName(@Param("tutorId") Long tutorId);

    @Query("SELECT p FROM Pet p WHERE p.species = :species AND p.active = true ORDER BY p.name")
    List<Pet> findActiveBySpecies(@Param("species") Species species);
}
