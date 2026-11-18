package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReparationService reparationService;

    public PaiementService(PaiementRepository paiementRepository, ReparationService reparationService) {
        this.paiementRepository = paiementRepository;
        this.reparationService = reparationService;
    }

    public List<Paiement> listerTous() {
        return paiementRepository.findAllByOrderByIdDesc();
    }

    public List<Paiement> listerParClient(Long clientId) {
        return paiementRepository.findByClientIdOrderByIdDesc(clientId);
    }

    public List<Paiement> listerParReparation(Long reparationId) {
        return paiementRepository.findByReparationIdOrderByIdDesc(reparationId);
    }

    public Paiement trouverParId(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable."));
    }

    public BigDecimal getMontantDu(Reparation reparation) {
        if (reparation == null || reparation.getDevis() == null || reparation.getDevis().getMontantTotal() == null) {
            return BigDecimal.ZERO;
        }
        return reparation.getDevis().getMontantTotal();
    }

    public BigDecimal getTotalPaye(Long reparationId) {
        BigDecimal somme = paiementRepository.sumMontantValideByReparationId(reparationId);
        return somme != null ? somme : BigDecimal.ZERO;
    }

    public BigDecimal getResteAPayer(Reparation reparation) {
        BigDecimal du = getMontantDu(reparation);
        BigDecimal paye = getTotalPaye(reparation.getId());
        BigDecimal reste = du.subtract(paye);
        return reste.compareTo(BigDecimal.ZERO) > 0 ? reste : BigDecimal.ZERO;
    }

    @Transactional
    public Paiement enregistrer(Long reparationId,
                                LocalDate datePaiement,
                                BigDecimal montant,
                                ModePaiement modePaiement,
                                String reference,
                                String observation,
                                Utilisateur utilisateur) {
        Reparation reparation = reparationService.trouverParId(reparationId);
        if (reparation.getStatut() == StatutReparation.ANNULEE) {
            throw new IllegalStateException("Impossible d'enregistrer un paiement sur une réparation annulée.");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être strictement positif.");
        }
        BigDecimal reste = getResteAPayer(reparation);
        if (montant.compareTo(reste) > 0) {
            throw new IllegalArgumentException("Le montant dépasse le reste à payer (" + reste.toPlainString() + " FCFA).");
        }
        if (modePaiement == null) {
            throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
        }

        Paiement paiement = new Paiement();
        paiement.setNumeroPaiement("TMP-" + System.nanoTime());
        paiement.setReparation(reparation);
        paiement.setClient(reparation.getClient());
        paiement.setDatePaiement(datePaiement != null ? datePaiement : LocalDate.now());
        paiement.setMontant(montant);
        paiement.setModePaiement(modePaiement);
        paiement.setReference(reference);
        paiement.setObservation(observation);
        paiement.setAnnule(false);
        paiement.setUtilisateurEnregistrement(utilisateur);

        Paiement sauvegarde = paiementRepository.save(paiement);
        sauvegarde.setNumeroPaiement(String.format("PAI-%d-%04d", sauvegarde.getDatePaiement().getYear(), sauvegarde.getId()));
        return paiementRepository.save(sauvegarde);
    }

    @Transactional
    public void annuler(Long paiementId, String motif, Utilisateur utilisateur) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif d'annulation est obligatoire pour conserver la traçabilité.");
        }
        Paiement paiement = trouverParId(paiementId);
        if (paiement.isAnnule()) {
            throw new IllegalStateException("Ce paiement est déjà annulé.");
        }
        paiement.setAnnule(true);
        paiement.setMotifAnnulation(motif.trim() + " (par " + utilisateur.getNomComplet() + ")");
        paiement.setDateAnnulation(LocalDateTime.now());
        paiementRepository.save(paiement);
    }
}
