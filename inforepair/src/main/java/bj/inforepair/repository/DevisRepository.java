package bj.inforepair.repository;

import bj.inforepair.entity.Devis;
import bj.inforepair.entity.StatutDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DevisRepository extends JpaRepository<Devis, Long> {
    List<Devis> findAllByOrderByIdDesc();
    List<Devis> findByClientIdOrderByIdDesc(Long clientId);
    List<Devis> findByMaterielIdOrderByIdDesc(Long materielId);
    List<Devis> findByStatutOrderByIdDesc(StatutDevis statut);
    boolean existsByClientId(Long clientId);
    boolean existsByMaterielId(Long materielId);
    long countByStatut(StatutDevis statut);

    @Query("SELECT COALESCE(SUM(d.montantTotal), 0) FROM Devis d WHERE d.statut = :statut " +
           "AND COALESCE(d.dateDecision, d.dateCreation) >= :debut AND COALESCE(d.dateDecision, d.dateCreation) <= :fin")
    BigDecimal sumMontantByStatutAndPeriode(@Param("statut") StatutDevis statut,
                                            @Param("debut") LocalDate debut,
                                            @Param("fin") LocalDate fin);
}
