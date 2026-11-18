package bj.inforepair.repository;

import bj.inforepair.entity.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    List<Intervention> findByReparationIdOrderByDateInterventionDesc(Long reparationId);
}
