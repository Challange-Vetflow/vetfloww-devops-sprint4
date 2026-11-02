package fiap.com.br.vetflow.repository;

import fiap.com.br.vetflow.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    boolean existsByNameIgnoreCase(String name);
}
