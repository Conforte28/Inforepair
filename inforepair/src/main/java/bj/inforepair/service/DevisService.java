package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.DevisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DevisService {

    private final DevisRepository devisRepository;
    private final MaterielService materielService;

    public DevisService(DevisRepository devisRepository, MaterielService materielService) {
        this.devisRepository = devisRepository;
        this.materielService = materielService;
    }

    public List<Devis> lister(StatutDevis statut, Utilisateur utilisateur) {
        if (utilisateur != null && utilisateur.getRole() == Role.TECHNICIEN) {
            return devisRepository.findByStatutOrderByIdDesc(StatutDevis.ACCEPTE);
        }
        if (statut != null) {
            return devisRepository.findByStatutOrderByIdDesc(statut);
        }
        return devisRepository.findAllByOrderByIdDesc();
    }

    public List<Devis> listerDevisAcceptes() {
        return devisRepository.findByStatutOrderByIdDesc(StatutDevis.ACCEPTE);
    }

    public List<Devis> listerParClient(Long clientId) {
        return devisRepository.findByClientIdOrderByIdDesc(clientId);
    }

    public List<Devis> listerParMateriel(Long materielId) {
        return devisRepository.findByMaterielIdOrderByIdDesc(materielId);
    }

    public Devis trouverParId(Long id) {
        return devisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devis introuvable."));
    }

    public boolean peutAcceder(Devis devis, Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }
        if (utilisateur.getRole() == Role.ADMIN || utilisateur.getRole() == Role.AGENT) {
            return true;
        }
        return devis.getStatut() == StatutDevis.ACCEPTE;
    }

    @Transactional
    public Devis enregistrer(Devis donnees,
                             List<String> designations,
                             List<Integer> quantites,
                             List<BigDecimal> prixUnitaires,
                             Utilisateur utilisateur) {
        if (donnees.getMateriel() == null || donnees.getMateriel().getId() == null) {
            throw new IllegalArgumentException("Le matériel concerné est obligatoire.");
        }
        Materiel materiel = materielService.trouverParId(donnees.getMateriel().getId());

        if (donnees.getDateCreation() == null) {
            donnees.setDateCreation(LocalDate.now());
        }
        if (donnees.getDateValidite() == null) {
            donnees.setDateValidite(donnees.getDateCreation().plusDays(15));
        }
        if (donnees.getDateValidite().isBefore(donnees.getDateCreation())) {
            throw new IllegalArgumentException("La date de validité doit être postérieure ou égale à la date de création.");
        }

        Devis cible;
        boolean creation = (donnees.getId() == null);
        if (creation) {
            cible = new Devis();
            cible.setNumeroDevis("TMP-" + System.nanoTime());
            cible.setStatut(StatutDevis.EN_ATTENTE);
            cible.setDevisParent(donnees.getDevisParent());
        } else {
            cible = trouverParId(donnees.getId());
            if (cible.getStatut() != StatutDevis.EN_ATTENTE) {
                throw new IllegalStateException("Un devis accepté, refusé ou expiré ne peut plus être modifié directement.");
            }
            cible.getLignes().clear();
        }

        if (donnees.getDiagnosticTechnique() == null || donnees.getDiagnosticTechnique().trim().isEmpty()) {
            throw new IllegalArgumentException("Le diagnostic technique est obligatoire.");
        }
        cible.setMateriel(materiel);
        cible.setClient(materiel.getClient());
        cible.setDateCreation(donnees.getDateCreation());
        cible.setDateValidite(donnees.getDateValidite());
        cible.setDiagnosticTechnique(donnees.getDiagnosticTechnique().trim());
        cible.setCoutMainOeuvre(donnees.getCoutMainOeuvre() != null ? donnees.getCoutMainOeuvre() : BigDecimal.ZERO);
        cible.setCoutPieces(donnees.getCoutPieces() != null ? donnees.getCoutPieces() : BigDecimal.ZERO);
        cible.setObservation(donnees.getObservation());

        List<LigneDevis> nouvellesLignes = new ArrayList<>();
        BigDecimal totalPrestations = BigDecimal.ZERO;

        if (designations != null) {
            for (int i = 0; i < designations.size(); i++) {
                String designation = designations.get(i);
                if (designation == null || designation.trim().isEmpty()) {
                    continue;
                }
                int qte = (quantites != null && i < quantites.size() && quantites.get(i) != null && quantites.get(i) > 0)
                        ? quantites.get(i) : 1;
                BigDecimal prix = (prixUnitaires != null && i < prixUnitaires.size() && prixUnitaires.get(i) != null)
                        ? prixUnitaires.get(i) : BigDecimal.ZERO;
                if (prix.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Le prix unitaire d'une prestation ne peut pas être négatif.");
                }
                BigDecimal sousTotal = prix.multiply(BigDecimal.valueOf(qte));

                LigneDevis ligne = new LigneDevis();
                ligne.setDevis(cible);
                ligne.setDesignation(designation.trim());
                ligne.setQuantite(qte);
                ligne.setPrixUnitaire(prix);
                ligne.setSousTotal(sousTotal);
                nouvellesLignes.add(ligne);
                totalPrestations = totalPrestations.add(sousTotal);
            }
        }

        cible.getLignes().addAll(nouvellesLignes);
        BigDecimal total = totalPrestations
                .add(cible.getCoutMainOeuvre())
                .add(cible.getCoutPieces());
        cible.setMontantTotal(total);

        Devis sauvegarde = devisRepository.save(cible);
        if (creation) {
            sauvegarde.setNumeroDevis(String.format("DEV-%d-%04d", sauvegarde.getDateCreation().getYear(), sauvegarde.getId()));
            sauvegarde = devisRepository.save(sauvegarde);
            if (materiel.getEtatActuel() == EtatMateriel.RECU
                    || materiel.getEtatActuel() == EtatMateriel.EN_ATTENTE_DIAGNOSTIC
                    || materiel.getEtatActuel() == EtatMateriel.DIAGNOSTIC_EFFECTUE) {
                materielService.changerEtat(materiel.getId(), EtatMateriel.DEVIS_EN_ATTENTE, utilisateur,
                        "Établissement du devis " + sauvegarde.getNumeroDevis());
            }
        }
        return sauvegarde;
    }

    @Transactional
    public Devis creerNouvelleVersion(Long devisOriginalId, Utilisateur utilisateur) {
        Devis original = trouverParId(devisOriginalId);
        Devis nouveau = new Devis();
        nouveau.setNumeroDevis("TMP-" + System.nanoTime());
        nouveau.setMateriel(original.getMateriel());
        nouveau.setClient(original.getClient());
        nouveau.setDateCreation(LocalDate.now());
        nouveau.setDateValidite(LocalDate.now().plusDays(15));
        nouveau.setDiagnosticTechnique(original.getDiagnosticTechnique());
        nouveau.setCoutMainOeuvre(original.getCoutMainOeuvre());
        nouveau.setCoutPieces(original.getCoutPieces());
        nouveau.setMontantTotal(original.getMontantTotal());
        nouveau.setObservation("Nouvelle version issue du devis " + original.getNumeroDevis());
        nouveau.setStatut(StatutDevis.EN_ATTENTE);
        nouveau.setDevisParent(original);

        for (LigneDevis l : original.getLignes()) {
            LigneDevis copie = new LigneDevis();
            copie.setDevis(nouveau);
            copie.setDesignation(l.getDesignation());
            copie.setQuantite(l.getQuantite());
            copie.setPrixUnitaire(l.getPrixUnitaire());
            copie.setSousTotal(l.getSousTotal());
            nouveau.getLignes().add(copie);
        }

        Devis sauvegarde = devisRepository.save(nouveau);
        sauvegarde.setNumeroDevis(String.format("DEV-%d-%04d", sauvegarde.getDateCreation().getYear(), sauvegarde.getId()));
        sauvegarde = devisRepository.save(sauvegarde);
        materielService.changerEtat(original.getMateriel().getId(), EtatMateriel.DEVIS_EN_ATTENTE, utilisateur,
                "Nouvelle version de devis créée : " + sauvegarde.getNumeroDevis());
        return sauvegarde;
    }

    @Transactional
    public void accepter(Long id, Utilisateur utilisateur) {
        Devis devis = trouverParId(id);
        if (devis.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seul un devis en attente peut être accepté.");
        }
        devis.setStatut(StatutDevis.ACCEPTE);
        devis.setDateDecision(LocalDate.now());
        devisRepository.save(devis);
        materielService.changerEtat(devis.getMateriel().getId(), EtatMateriel.DEVIS_ACCEPTE, utilisateur,
                "Devis " + devis.getNumeroDevis() + " accepté par le client.");
    }

    @Transactional
    public void refuser(Long id, Utilisateur utilisateur) {
        Devis devis = trouverParId(id);
        if (devis.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seul un devis en attente peut être refusé.");
        }
        devis.setStatut(StatutDevis.REFUSE);
        devis.setDateDecision(LocalDate.now());
        devisRepository.save(devis);
        materielService.changerEtat(devis.getMateriel().getId(), EtatMateriel.DEVIS_REFUSE, utilisateur,
                "Devis " + devis.getNumeroDevis() + " refusé par le client.");
    }

    @Transactional
    public void marquerExpire(Long id) {
        Devis devis = trouverParId(id);
        if (devis.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seul un devis en attente peut être marqué comme expiré.");
        }
        devis.setStatut(StatutDevis.EXPIRE);
        devisRepository.save(devis);
    }
}
