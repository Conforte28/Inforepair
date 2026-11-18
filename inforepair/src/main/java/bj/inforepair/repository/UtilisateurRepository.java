package bj.inforepair.repository;

import bj.inforepair.entity.Role;
import bj.inforepair.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Utilisateur> findByRoleAndActifTrueOrderByNomCompletAsc(Role role);
    List<Utilisateur> findByActifTrueOrderByNomCompletAsc();
    long countByRoleAndActifTrue(Role role);
    List<Utilisateur> findAllByOrderByNomCompletAsc();
}
