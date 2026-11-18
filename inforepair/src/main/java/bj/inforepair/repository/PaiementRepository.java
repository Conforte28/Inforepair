package bj.inforepair.repository;

import bj.inforepair.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findAllByOrderByIdDesc();
    List<Paiement> findByClientIdOrderByIdDesc(Long clientId);
    List<Paiement> findByReparationIdOrderByIdDesc(Long reparationId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.reparation.id = :reparationId AND p.annule = false")
    BigDecimal sumMontantValideByReparationId(@Param("reparationId") Long reparationId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.annule = false " +
           "AND p.datePaiement >= :debut AND p.datePaiement <= :fin")
    BigDecimal sumMontantValideByPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
