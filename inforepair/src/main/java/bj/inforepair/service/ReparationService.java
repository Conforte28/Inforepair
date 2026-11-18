package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.InterventionRepository;
import bj.inforepair.repository.ReparationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class ReparationService {

    public static final Set<StatutReparation> STATUTS_ACTIFS = EnumSet.of(
            StatutReparation.NON_COMMENCEE,
            StatutReparation.EN_COURS,
            StatutReparation.EN_ATTENTE_PIECES,
            StatutReparation.CONTROLE_QUALITE
    );

    private final ReparationRepository reparationRepository;
    private final InterventionRepository interventionRepository;
    private final DevisService devisService;
    private final MaterielService materielService;
    private final UtilisateurService utilisateurService;

    public ReparationService(ReparationRepository reparationRepository,
                             InterventionRepository interventionRepository,
                             DevisService devisService,
                             MaterielService materielService,
                             UtilisateurService utilisateurService) {
        this.reparationRepository = reparationRepository;
        this.interventionRepository = interventionRepository;
        this.devisService = devisService;
        this.materielService = materielService;
        this.utilisateurService = utilisateurService;
    }

    public List<Reparation> lister(Utilisateur utilisateur) {
        if (utilisateur != null && utilisateur.getRole() == Role.TECHNICIEN) {
            return reparationRepository.findByTechnicienIdOrderByIdDesc(utilisateur.getId());
        }
        return reparationRepository.findAllByOrderByIdDesc();
    }

    public List<Reparation> listerToutes() {
        return reparationRepository.findAllByOrderByIdDesc();
    }

    public List<Reparation> listerParClient(Long clientId) {
        return reparationRepository.findByClientIdOrderByIdDesc(clientId);
    }

    public List<Reparation> listerParMateriel(Long materielId) {
        return reparationRepository.findByMaterielIdOrderByIdDesc(materielId);
    }

    public Reparation trouverParId(Long id) {
        return reparationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réparation introuvable."));
    }

    public boolean peutAcceder(Reparation reparation, Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }
        if (utilisateur.getRole() == Role.ADMIN || utilisateur.getRole() == Role.AGENT) {
            return true;
        }
        return reparation.getTechnicien() != null && reparation.getTechnicien().getId().equals(utilisateur.getId());
    }

    public List<Intervention> listerInterventions(Long reparationId) {
        return interventionRepository.findByReparationIdOrderByDateInterventionDesc(reparationId);
    }

    @Transactional
    public Reparation demarrerDepuisDevis(Long devisId,
                                          Long technicienId,
                                          LocalDate dateDebut,
                                          String observation,
                                          Utilisateur utilisateur) {
        Devis devis = devisService.trouverParId(devisId);
        if (devis.getStatut() != StatutDevis.ACCEPTE) {
            throw new IllegalStateException("Une réparation ne peut démarrer qu'à partir d'un devis accepté.");
        }
        Materiel materiel = devis.getMateriel();
        if (reparationRepository.existsByMaterielIdAndStatutIn(materiel.getId(), STATUTS_ACTIFS)) {
            throw new IllegalStateException("Ce matériel possède déjà une réparation active en cours.");
        }

        Reparation reparation = new Reparation();
        reparation.setNumeroReparation("TMP-" + System.nanoTime());
        reparation.setDevis(devis);
        reparation.setMateriel(materiel);
        reparation.setClient(devis.getClient());
        reparation.setDateDebut(dateDebut != null ? dateDebut : LocalDate.now());
        reparation.setCoutMainOeuvre(devis.getCoutMainOeuvre() != null ? devis.getCoutMainOeuvre() : BigDecimal.ZERO);
        reparation.setCoutPieces(devis.getCoutPieces() != null ? devis.getCoutPieces() : BigDecimal.ZERO);
        reparation.setObservation(observation);

        if (technicienId != null) {
            Utilisateur tech = utilisateurService.trouverParId(technicienId);
            reparation.setTechnicien(tech);
            reparation.setStatut(StatutReparation.EN_COURS);
        } else {
            reparation.setStatut(StatutReparation.NON_COMMENCEE);
        }

        Reparation sauvegarde = reparationRepository.save(reparation);
        sauvegarde.setNumeroReparation(String.format("REP-%d-%04d", sauvegarde.getDateDebut().getYear(), sauvegarde.getId()));
        sauvegarde = reparationRepository.save(sauvegarde);

        materielService.changerEtat(materiel.getId(), EtatMateriel.EN_REPARATION, utilisateur,
                "Démarrage de la réparation " + sauvegarde.getNumeroReparation());
        return sauvegarde;
    }

    @Transactional
    public Reparation mettreAJour(Long reparationId,
                                    Long technicienId,
                                    StatutReparation nouveauStatut,
                                    LocalDate dateFin,
                                    String descriptionTravail,
                                    String piecesUtilisees,
                                    BigDecimal coutPieces,
                                    BigDecimal coutMainOeuvre,
                                    String observation,
                                    Utilisateur utilisateur) {
        Reparation existante = trouverParId(reparationId);
        StatutReparation ancienStatut = existante.getStatut();

        if (ancienStatut == StatutReparation.PRETE_A_ETRE_RETIREE || ancienStatut == StatutReparation.ANNULEE) {
            throw new IllegalStateException("Cette réparation est déjà clôturée ou annulée et ne peut plus être modifiée.");
        }

        if (ancienStatut == StatutReparation.TERMINEE) {
            if (nouveauStatut != StatutReparation.PRETE_A_ETRE_RETIREE && nouveauStatut != StatutReparation.TERMINEE) {
                throw new IllegalStateException("Une réparation terminée ne peut être passée qu'à l'état Prête à être retirée.");
            }
            existante.setStatut(nouveauStatut);
            if (observation != null && !observation.isBlank()) {
                existante.setObservation(observation);
            }
            Reparation maj = reparationRepository.save(existante);
            if (nouveauStatut == StatutReparation.PRETE_A_ETRE_RETIREE) {
                materielService.changerEtat(existante.getMateriel().getId(), EtatMateriel.PRET_A_ETRE_RETIRE, utilisateur,
                        "Réparation " + existante.getNumeroReparation() + " prête pour retrait.");
            }
            return maj;
        }

        if (dateFin != null && dateFin.isBefore(existante.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }

        if (utilisateur.getRole() != Role.TECHNICIEN) {
            if (technicienId != null) {
                existante.setTechnicien(utilisateurService.trouverParId(technicienId));
            } else {
                existante.setTechnicien(null);
            }
        }

        existante.setDescriptionTravail(descriptionTravail);
        existante.setPiecesUtilisees(piecesUtilisees);
        existante.setCoutPieces(coutPieces != null ? coutPieces : BigDecimal.ZERO);
        existante.setCoutMainOeuvre(coutMainOeuvre != null ? coutMainOeuvre : BigDecimal.ZERO);
        existante.setObservation(observation);

        if (nouveauStatut != null) {
            existante.setStatut(nouveauStatut);
        }

        if ((existante.getStatut() == StatutReparation.TERMINEE || existante.getStatut() == StatutReparation.PRETE_A_ETRE_RETIREE)
                && dateFin == null) {
            existante.setDateFin(LocalDate.now());
        } else {
            existante.setDateFin(dateFin);
        }

        Reparation sauvegarde = reparationRepository.save(existante);
        synchroniserEtatMateriel(sauvegarde, ancienStatut, utilisateur);
        return sauvegarde;
    }

    @Transactional
    public void ajouterIntervention(Long reparationId,
                                    LocalDateTime dateIntervention,
                                    String descriptionTravail,
                                    String piecesUtilisees,
                                    String observation,
                                    Utilisateur utilisateur) {
        Reparation reparation = trouverParId(reparationId);
        if (reparation.getStatut() == StatutReparation.ANNULEE) {
            throw new IllegalStateException("Impossible d'ajouter une intervention sur une réparation annulée.");
        }
        if (descriptionTravail == null || descriptionTravail.trim().isEmpty()) {
            throw new IllegalArgumentException("La description du travail effectué est obligatoire.");
        }

        Intervention intervention = new Intervention();
        intervention.setReparation(reparation);
        intervention.setDateIntervention(dateIntervention != null ? dateIntervention : LocalDateTime.now());
        intervention.setTechnicien(utilisateur);
        intervention.setDescriptionTravail(descriptionTravail.trim());
        intervention.setPiecesUtilisees(piecesUtilisees);
        intervention.setObservation(observation);
        interventionRepository.save(intervention);

        if (reparation.getStatut() == StatutReparation.NON_COMMENCEE) {
            reparation.setStatut(StatutReparation.EN_COURS);
            reparationRepository.save(reparation);
            materielService.changerEtat(reparation.getMateriel().getId(), EtatMateriel.EN_REPARATION, utilisateur,
                    "Première intervention enregistrée sur " + reparation.getNumeroReparation());
        }
    }

    @Transactional
    public void remettreAuClient(Long reparationId, Utilisateur utilisateur) {
        Reparation reparation = trouverParId(reparationId);
        if (reparation.getStatut() != StatutReparation.TERMINEE
                && reparation.getStatut() != StatutReparation.PRETE_A_ETRE_RETIREE) {
            throw new IllegalStateException("La réparation doit être terminée ou prête avant la remise au client.");
        }
        if (reparation.getStatut() == StatutReparation.TERMINEE) {
            reparation.setStatut(StatutReparation.PRETE_A_ETRE_RETIREE);
            if (reparation.getDateFin() == null) {
                reparation.setDateFin(LocalDate.now());
            }
            reparationRepository.save(reparation);
        }
        materielService.changerEtat(reparation.getMateriel().getId(), EtatMateriel.REMIS_AU_CLIENT, utilisateur,
                "Matériel remis au client suite à la réparation " + reparation.getNumeroReparation());
    }

    private void synchroniserEtatMateriel(Reparation reparation, StatutReparation ancienStatut, Utilisateur utilisateur) {
        if (ancienStatut == reparation.getStatut()) {
            return;
        }
        Long materielId = reparation.getMateriel().getId();
        String msg = "Réparation " + reparation.getNumeroReparation() + " : " + reparation.getStatut().getLibelle();
        switch (reparation.getStatut()) {
            case NON_COMMENCEE, EN_COURS, CONTROLE_QUALITE ->
                    materielService.changerEtat(materielId, EtatMateriel.EN_REPARATION, utilisateur, msg);
            case EN_ATTENTE_PIECES ->
                    materielService.changerEtat(materielId, EtatMateriel.EN_ATTENTE_PIECES, utilisateur, msg);
            case TERMINEE ->
                    materielService.changerEtat(materielId, EtatMateriel.REPARATION_TERMINEE, utilisateur, msg);
            case PRETE_A_ETRE_RETIREE ->
                    materielService.changerEtat(materielId, EtatMateriel.PRET_A_ETRE_RETIRE, utilisateur, msg);
            case ANNULEE ->
                    materielService.changerEtat(materielId, EtatMateriel.DIAGNOSTIC_EFFECTUE, utilisateur, msg);
        }
    }
}
