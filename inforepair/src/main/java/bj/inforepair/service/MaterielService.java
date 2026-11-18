package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterielService {

    private final MaterielRepository materielRepository;
    private final HistoriqueMaterielRepository historiqueRepository;
    private final DevisRepository devisRepository;
    private final ReparationRepository reparationRepository;
    private final PlainteRepository plainteRepository;

    public MaterielService(MaterielRepository materielRepository,
                           HistoriqueMaterielRepository historiqueRepository,
                           DevisRepository devisRepository,
                           ReparationRepository reparationRepository,
                           PlainteRepository plainteRepository) {
        this.materielRepository = materielRepository;
        this.historiqueRepository = historiqueRepository;
        this.devisRepository = devisRepository;
        this.reparationRepository = reparationRepository;
        this.plainteRepository = plainteRepository;
    }

    public List<Materiel> filtrer(String terme, TypeMateriel typeMateriel, EtatMateriel etatActuel, Utilisateur utilisateur) {
        List<Materiel> base;
        if (utilisateur != null && utilisateur.getRole() == Role.TECHNICIEN) {
            base = materielRepository.findByTechnicienId(utilisateur.getId());
        } else {
            base = materielRepository.findAllByOrderByIdDesc();
        }

        String recherche = terme != null ? terme.trim().toLowerCase() : "";
        return base.stream()
                .filter(m -> recherche.isEmpty()
                        || m.getNumeroDossier().toLowerCase().contains(recherche)
                        || m.getMarque().toLowerCase().contains(recherche)
                        || m.getModele().toLowerCase().contains(recherche)
                        || m.getClient().getNomComplet().toLowerCase().contains(recherche))
                .filter(m -> typeMateriel == null || m.getTypeMateriel() == typeMateriel)
                .filter(m -> etatActuel == null || m.getEtatActuel() == etatActuel)
                .collect(Collectors.toList());
    }

    public List<Materiel> listerTous() {
        return materielRepository.findAllByOrderByIdDesc();
    }

    public Materiel trouverParId(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matériel introuvable."));
    }

    public boolean peutAcceder(Materiel materiel, Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }
        if (utilisateur.getRole() == Role.ADMIN || utilisateur.getRole() == Role.AGENT) {
            return true;
        }
        return reparationRepository.findByMaterielIdOrderByIdDesc(materiel.getId())
                .stream()
                .anyMatch(r -> r.getTechnicien() != null && r.getTechnicien().getId().equals(utilisateur.getId()));
    }

    public List<HistoriqueMateriel> listerHistorique(Long materielId) {
        return historiqueRepository.findByMaterielIdOrderByDateModificationDesc(materielId);
    }

    @Transactional
    public Materiel enregistrer(Materiel materiel, Utilisateur utilisateur) {
        if (materiel.getId() == null) {
            if (materiel.getDateDepot() == null) {
                materiel.setDateDepot(LocalDate.now());
            }
            if (materiel.getEtatActuel() == null) {
                materiel.setEtatActuel(EtatMateriel.RECU);
            }
            materiel.setNumeroDossier("TMP-" + System.nanoTime());
            Materiel sauvegarde = materielRepository.save(materiel);
            sauvegarde.setNumeroDossier(String.format("MAT-%d-%04d", sauvegarde.getDateDepot().getYear(), sauvegarde.getId()));
            sauvegarde = materielRepository.save(sauvegarde);

            HistoriqueMateriel historique = new HistoriqueMateriel();
            historique.setMateriel(sauvegarde);
            historique.setAncienEtat(null);
            historique.setNouvelEtat(sauvegarde.getEtatActuel());
            historique.setDateModification(LocalDateTime.now());
            historique.setUtilisateur(utilisateur);
            historique.setObservation("Enregistrement initial du matériel.");
            historiqueRepository.save(historique);
            return sauvegarde;
        }

        Materiel existant = trouverParId(materiel.getId());
        existant.setClient(materiel.getClient());
        existant.setTypeMateriel(materiel.getTypeMateriel());
        existant.setMarque(materiel.getMarque().trim());
        existant.setModele(materiel.getModele().trim());
        existant.setNumeroSerie(materiel.getNumeroSerie());
        existant.setAccessoiresDeposes(materiel.getAccessoiresDeposes());
        existant.setDateDepot(materiel.getDateDepot());
        existant.setDescriptionProbleme(materiel.getDescriptionProbleme());
        existant.setEtatPhysique(materiel.getEtatPhysique());
        existant.setSessionCodeFourni(materiel.isSessionCodeFourni());
        existant.setNoteAccesSession(materiel.getNoteAccesSession());
        existant.setObservation(materiel.getObservation());
        return materielRepository.save(existant);
    }

    @Transactional
    public void changerEtat(Long materielId, EtatMateriel nouvelEtat, Utilisateur utilisateur, String observation) {
        Materiel materiel = trouverParId(materielId);
        EtatMateriel ancienEtat = materiel.getEtatActuel();
        if (ancienEtat == nouvelEtat && (observation == null || observation.isBlank())) {
            return;
        }
        materiel.setEtatActuel(nouvelEtat);
        materielRepository.save(materiel);

        HistoriqueMateriel historique = new HistoriqueMateriel();
        historique.setMateriel(materiel);
        historique.setAncienEtat(ancienEtat);
        historique.setNouvelEtat(nouvelEtat);
        historique.setDateModification(LocalDateTime.now());
        historique.setUtilisateur(utilisateur);
        historique.setObservation(observation != null && !observation.isBlank() ? observation.trim() : null);
        historiqueRepository.save(historique);
    }

    public boolean peutSupprimer(Long materielId) {
        return !devisRepository.existsByMaterielId(materielId)
                && !reparationRepository.existsByMaterielId(materielId)
                && !plainteRepository.existsByMaterielId(materielId);
    }

    @Transactional
    public void supprimer(Long materielId) {
        if (!peutSupprimer(materielId)) {
            throw new IllegalStateException("Ce matériel possède des devis, réparations ou plaintes liés et ne peut pas être supprimé.");
        }
        List<HistoriqueMateriel> historiques = historiqueRepository.findByMaterielIdOrderByDateModificationDesc(materielId);
        historiqueRepository.deleteAll(historiques);
        materielRepository.deleteById(materielId);
    }
}
