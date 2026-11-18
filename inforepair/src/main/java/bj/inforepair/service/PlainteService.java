package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.HistoriquePlainteRepository;
import bj.inforepair.repository.PlainteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlainteService {

    private final PlainteRepository plainteRepository;
    private final HistoriquePlainteRepository historiqueRepository;
    private final UtilisateurService utilisateurService;

    public PlainteService(PlainteRepository plainteRepository,
                          HistoriquePlainteRepository historiqueRepository,
                          UtilisateurService utilisateurService) {
        this.plainteRepository = plainteRepository;
        this.historiqueRepository = historiqueRepository;
        this.utilisateurService = utilisateurService;
    }

    public List<Plainte> filtrer(String terme, StatutPlainte statut, Utilisateur utilisateur) {
        List<Plainte> base;
        if (utilisateur != null && utilisateur.getRole() == Role.TECHNICIEN) {
            base = plainteRepository.findByTechnicienId(utilisateur.getId());
        } else {
            base = plainteRepository.findAllByOrderByIdDesc();
        }
        String recherche = terme != null ? terme.trim().toLowerCase() : "";
        return base.stream()
                .filter(p -> recherche.isEmpty()
                        || p.getNumeroPlainte().toLowerCase().contains(recherche)
                        || p.getObjet().toLowerCase().contains(recherche)
                        || p.getClient().getNomComplet().toLowerCase().contains(recherche))
                .filter(p -> statut == null || p.getStatut() == statut)
                .collect(Collectors.toList());
    }

    public List<Plainte> listerParClient(Long clientId) {
        return plainteRepository.findByClientIdOrderByIdDesc(clientId);
    }

    public List<Plainte> listerParMateriel(Long materielId) {
        return plainteRepository.findByMaterielIdOrderByIdDesc(materielId);
    }

    public Plainte trouverParId(Long id) {
        return plainteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plainte introuvable."));
    }

    public boolean peutAcceder(Plainte plainte, Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }
        if (utilisateur.getRole() == Role.ADMIN || utilisateur.getRole() == Role.AGENT) {
            return true;
        }
        return plainte.getReparation() != null
                && plainte.getReparation().getTechnicien() != null
                && plainte.getReparation().getTechnicien().getId().equals(utilisateur.getId());
    }

    public List<HistoriquePlainte> listerHistorique(Long plainteId) {
        return historiqueRepository.findByPlainteIdOrderByDateModificationDesc(plainteId);
    }

    @Transactional
    public Plainte enregistrer(Plainte plainte, Utilisateur utilisateur) {
        if (plainte.getId() == null) {
            if (plainte.getDateDepot() == null) {
                plainte.setDateDepot(LocalDate.now());
            }
            if (plainte.getStatut() == null) {
                plainte.setStatut(StatutPlainte.NOUVELLE);
            }
            if (plainte.getReparation() != null && plainte.getMateriel() == null) {
                plainte.setMateriel(plainte.getReparation().getMateriel());
            }
            plainte.setNumeroPlainte("TMP-" + System.nanoTime());
            Plainte sauvegarde = plainteRepository.save(plainte);
            sauvegarde.setNumeroPlainte(String.format("PLA-%d-%04d", sauvegarde.getDateDepot().getYear(), sauvegarde.getId()));
            sauvegarde = plainteRepository.save(sauvegarde);

            HistoriquePlainte hist = new HistoriquePlainte();
            hist.setPlainte(sauvegarde);
            hist.setAncienStatut(null);
            hist.setNouveauStatut(sauvegarde.getStatut());
            hist.setDateModification(LocalDateTime.now());
            hist.setUtilisateur(utilisateur);
            hist.setActionRealisee("Enregistrement initial de la plainte.");
            hist.setObservation(sauvegarde.getObservation());
            historiqueRepository.save(hist);
            return sauvegarde;
        }

        Plainte existante = trouverParId(plainte.getId());
        if (existante.getStatut() == StatutPlainte.CLOTUREE) {
            throw new IllegalStateException("Une plainte clôturée ne peut plus être modifiée. Ajoutez une action de suivi si le client revient.");
        }
        existante.setClient(plainte.getClient());
        existante.setMateriel(plainte.getMateriel());
        existante.setReparation(plainte.getReparation());
        existante.setDateDepot(plainte.getDateDepot());
        existante.setTypePlainte(plainte.getTypePlainte());
        existante.setObjet(plainte.getObjet().trim());
        existante.setDescription(plainte.getDescription().trim());
        existante.setResponsable(plainte.getResponsable());
        existante.setObservation(plainte.getObservation());
        return plainteRepository.save(existante);
    }

    @Transactional
    public void traiter(Long plainteId,
                        StatutPlainte nouveauStatut,
                        Long responsableId,
                        String actionRealisee,
                        String solutionApportee,
                        String observation,
                        Utilisateur utilisateur) {
        Plainte plainte = trouverParId(plainteId);
        StatutPlainte ancienStatut = plainte.getStatut();

        if (ancienStatut == StatutPlainte.CLOTUREE) {
            if (actionRealisee == null || actionRealisee.trim().isEmpty()) {
                throw new IllegalArgumentException("Veuillez préciser la nouvelle action de suivi à enregistrer.");
            }
            HistoriquePlainte suivi = new HistoriquePlainte();
            suivi.setPlainte(plainte);
            suivi.setAncienStatut(StatutPlainte.CLOTUREE);
            suivi.setNouveauStatut(nouveauStatut != null ? nouveauStatut : StatutPlainte.CLOTUREE);
            suivi.setDateModification(LocalDateTime.now());
            suivi.setUtilisateur(utilisateur);
            suivi.setActionRealisee(actionRealisee.trim());
            suivi.setObservation(observation);
            historiqueRepository.save(suivi);

            if (nouveauStatut != null && nouveauStatut != StatutPlainte.CLOTUREE) {
                plainte.setStatut(nouveauStatut);
                plainteRepository.save(plainte);
            }
            return;
        }

        if (responsableId != null) {
            plainte.setResponsable(utilisateurService.trouverParId(responsableId));
        }
        if (solutionApportee != null && !solutionApportee.trim().isEmpty()) {
            plainte.setSolutionApportee(solutionApportee.trim());
        }
        if (observation != null && !observation.trim().isEmpty()) {
            plainte.setObservation(observation.trim());
        }

        StatutPlainte statutFinal = nouveauStatut != null ? nouveauStatut : ancienStatut;
        plainte.setStatut(statutFinal);
        if ((statutFinal == StatutPlainte.RESOLUE || statutFinal == StatutPlainte.CLOTUREE)
                && plainte.getDateResolution() == null) {
            plainte.setDateResolution(LocalDate.now());
        }
        plainteRepository.save(plainte);

        HistoriquePlainte hist = new HistoriquePlainte();
        hist.setPlainte(plainte);
        hist.setAncienStatut(ancienStatut);
        hist.setNouveauStatut(statutFinal);
        hist.setDateModification(LocalDateTime.now());
        hist.setUtilisateur(utilisateur);
        hist.setActionRealisee(actionRealisee != null && !actionRealisee.trim().isEmpty()
                ? actionRealisee.trim()
                : "Mise à jour du traitement (" + statutFinal.getLibelle() + ").");
        hist.setObservation(observation);
        historiqueRepository.save(hist);
    }
}
