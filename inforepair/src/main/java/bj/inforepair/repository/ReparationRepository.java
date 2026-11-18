package bj.inforepair.repository;

import bj.inforepair.entity.Reparation;
import bj.inforepair.entity.StatutReparation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface ReparationRepository extends JpaRepository<Reparation, Long> {
    List<Reparation> findAllByOrderByIdDesc();
    List<Reparation> findTop5ByOrderByIdDesc();
    List<Reparation> findByClientIdOrderByIdDesc(Long clientId);
    List<Reparation> findByMaterielIdOrderByIdDesc(Long materielId);
    List<Reparation> findByTechnicienIdOrderByIdDesc(Long technicienId);
    boolean existsByClientId(Long clientId);
    boolean existsByMaterielId(Long materielId);
    boolean existsByMaterielIdAndStatutIn(Long materielId, Collection<StatutReparation> statuts);
    boolean existsByDevisId(Long devisId);
    long countByStatutIn(Collection<StatutReparation> statuts);
}
