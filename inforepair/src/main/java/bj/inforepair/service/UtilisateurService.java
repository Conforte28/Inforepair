package bj.inforepair.service;

import bj.inforepair.entity.Role;
import bj.inforepair.entity.Utilisateur;
import bj.inforepair.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAllByOrderByNomCompletAsc();
    }

    public List<Utilisateur> listerTechniciensActifs() {
        return utilisateurRepository.findByRoleAndActifTrueOrderByNomCompletAsc(Role.TECHNICIEN);
    }

    public List<Utilisateur> listerActifs() {
        return utilisateurRepository.findByActifTrueOrderByNomCompletAsc();
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return utilisateurRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    public Utilisateur creer(Utilisateur utilisateur, String motDePasseClair) {
        if (utilisateurRepository.existsByEmailIgnoreCase(utilisateur.getEmail().trim())) {
            throw new IllegalArgumentException("Cette adresse électronique est déjà utilisée.");
        }
        if (motDePasseClair == null || motDePasseClair.trim().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        utilisateur.setEmail(utilisateur.getEmail().trim().toLowerCase());
        utilisateur.setNomComplet(utilisateur.getNomComplet().trim());
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasseClair));
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur modifier(Long id, Utilisateur donnees) {
        Utilisateur existant = trouverParId(id);
        String nouvelEmail = donnees.getEmail().trim().toLowerCase();
        Optional<Utilisateur> autre = utilisateurRepository.findByEmailIgnoreCase(nouvelEmail);
        if (autre.isPresent() && !autre.get().getId().equals(id)) {
            throw new IllegalArgumentException("Cette adresse électronique est déjà utilisée par un autre compte.");
        }

        boolean etaitAdminActif = existant.getRole() == Role.ADMIN && existant.isActif();
        boolean resteAdminActif = donnees.getRole() == Role.ADMIN && donnees.isActif();
        if (etaitAdminActif && !resteAdminActif && utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN) <= 1) {
            throw new IllegalStateException("Impossible de désactiver ou de rétrograder le dernier administrateur actif.");
        }

        existant.setNomComplet(donnees.getNomComplet().trim());
        existant.setEmail(nouvelEmail);
        existant.setTelephone(donnees.getTelephone());
        existant.setRole(donnees.getRole());
        existant.setActif(donnees.isActif());
        return utilisateurRepository.save(existant);
    }

    @Transactional
    public void basculerActif(Long id) {
        Utilisateur existant = trouverParId(id);
        if (existant.getRole() == Role.ADMIN && existant.isActif() && utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN) <= 1) {
            throw new IllegalStateException("Impossible de désactiver le dernier administrateur actif.");
        }
        existant.setActif(!existant.isActif());
        utilisateurRepository.save(existant);
    }

    @Transactional
    public void changerMotDePasse(Long id, String nouveauMotDePasse) {
        if (nouveauMotDePasse == null || nouveauMotDePasse.trim().length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères.");
        }
        Utilisateur existant = trouverParId(id);
        existant.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(existant);
    }

    @Transactional
    public void changerPropreMotDePasse(Utilisateur utilisateur, String motDePasseActuel, String nouveauMotDePasse) {
        if (!passwordEncoder.matches(motDePasseActuel, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        changerMotDePasse(utilisateur.getId(), nouveauMotDePasse);
    }
}
