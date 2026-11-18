package bj.inforepair.repository;

import bj.inforepair.entity.HistoriquePlainte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriquePlainteRepository extends JpaRepository<HistoriquePlainte, Long> {
    List<HistoriquePlainte> findByPlainteIdOrderByDateModificationDesc(Long plainteId);
}
