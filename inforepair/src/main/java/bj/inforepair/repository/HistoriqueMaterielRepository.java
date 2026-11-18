package bj.inforepair.repository;

import bj.inforepair.entity.HistoriqueMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueMaterielRepository extends JpaRepository<HistoriqueMateriel, Long> {
    List<HistoriqueMateriel> findByMaterielIdOrderByDateModificationDesc(Long materielId);
    List<HistoriqueMateriel> findTop8ByOrderByDateModificationDesc();
}
