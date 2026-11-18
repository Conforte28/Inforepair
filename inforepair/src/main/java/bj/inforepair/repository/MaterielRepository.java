package bj.inforepair.repository;

import bj.inforepair.entity.EtatMateriel;
import bj.inforepair.entity.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findAllByOrderByIdDesc();
    List<Materiel> findTop5ByOrderByIdDesc();
    List<Materiel> findByClientIdOrderByIdDesc(Long clientId);
    boolean existsByClientId(Long clientId);
    long countByEtatActuel(EtatMateriel etatActuel);

    @Query("SELECT DISTINCT r.materiel FROM Reparation r WHERE r.technicien.id = :technicienId ORDER BY r.materiel.id DESC")
    List<Materiel> findByTechnicienId(@Param("technicienId") Long technicienId);
}
