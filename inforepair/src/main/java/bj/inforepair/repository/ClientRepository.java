package bj.inforepair.repository;

import bj.inforepair.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByOrderByIdDesc();
    List<Client> findByActifTrueOrderByNomCompletAsc();

    @Query("SELECT c FROM Client c WHERE LOWER(c.nomComplet) LIKE LOWER(CONCAT('%', :terme, '%')) " +
           "OR LOWER(c.telephone) LIKE LOWER(CONCAT('%', :terme, '%')) " +
           "OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :terme, '%')) " +
           "OR LOWER(c.codeClient) LIKE LOWER(CONCAT('%', :terme, '%')) ORDER BY c.id DESC")
    List<Client> rechercher(@Param("terme") String terme);
}
