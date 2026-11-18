package bj.inforepair.repository;

import bj.inforepair.entity.Plainte;
import bj.inforepair.entity.StatutPlainte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;

public interface PlainteRepository extends JpaRepository<Plainte, Long> {
    List<Plainte> findAllByOrderByIdDesc();
    List<Plainte> findTop5ByOrderByIdDesc();
    List<Plainte> findByClientIdOrderByIdDesc(Long clientId);
    List<Plainte> findByMaterielIdOrderByIdDesc(Long materielId);
    boolean existsByClientId(Long clientId);
    boolean existsByMaterielId(Long materielId);
    long countByStatutIn(Collection<StatutPlainte> statuts);

    @Query("SELECT p FROM Plainte p WHERE p.reparation IS NOT NULL AND p.reparation.technicien.id = :technicienId ORDER BY p.id DESC")
    List<Plainte> findByTechnicienId(@Param("technicienId") Long technicienId);
}
