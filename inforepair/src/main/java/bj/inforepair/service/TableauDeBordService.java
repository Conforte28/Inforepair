package bj.inforepair.service;

import bj.inforepair.entity.*;
import bj.inforepair.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
public class TableauDeBordService {

    private final MaterielRepository materielRepository;
    private final DevisRepository devisRepository;
    private final ReparationRepository reparationRepository;
    private final PlainteRepository plainteRepository;
    private final PaiementRepository paiementRepository;
    private final HistoriqueMaterielRepository historiqueMaterielRepository;

    public TableauDeBordService(MaterielRepository materielRepository,
                                DevisRepository devisRepository,
                                ReparationRepository reparationRepository,
                                PlainteRepository plainteRepository,
                                PaiementRepository paiementRepository,
                                HistoriqueMaterielRepository historiqueMaterielRepository) {
        this.materielRepository = materielRepository;
        this.devisRepository = devisRepository;
        this.reparationRepository = reparationRepository;
        this.plainteRepository = plainteRepository;
        this.paiementRepository = paiementRepository;
        this.historiqueMaterielRepository = historiqueMaterielRepository;
    }

    public long getTotalMateriels() {
        return materielRepository.count();
    }

    public long getMaterielsEnAttenteDiagnostic() {
        return materielRepository.countByEtatActuel(EtatMateriel.EN_ATTENTE_DIAGNOSTIC);
    }

    public long getDevisEnAttente() {
        return devisRepository.countByStatut(StatutDevis.EN_ATTENTE);
    }

    public long getReparationsEnCours() {
        return reparationRepository.countByStatutIn(EnumSet.of(
                StatutReparation.NON_COMMENCEE,
                StatutReparation.EN_COURS,
                StatutReparation.EN_ATTENTE_PIECES,
                StatutReparation.CONTROLE_QUALITE
        ));
    }

    public long getReparationsTerminees() {
        return reparationRepository.countByStatutIn(EnumSet.of(
                StatutReparation.TERMINEE,
                StatutReparation.PRETE_A_ETRE_RETIREE
        ));
    }

    public long getMaterielsRemisAuxClients() {
        return materielRepository.countByEtatActuel(EtatMateriel.REMIS_AU_CLIENT);
    }

    public long getPlaintesNonResolues() {
        return plainteRepository.countByStatutIn(EnumSet.of(
                StatutPlainte.NOUVELLE,
                StatutPlainte.EN_COURS_TRAITEMENT,
                StatutPlainte.EN_ATTENTE_CLIENT
        ));
    }

    public BigDecimal getMontantDevisAcceptesMoisCourant() {
        LocalDate maintenant = LocalDate.now();
        LocalDate debutMois = maintenant.withDayOfMonth(1);
        LocalDate finMois = maintenant.withDayOfMonth(maintenant.lengthOfMonth());
        BigDecimal total = devisRepository.sumMontantByStatutAndPeriode(StatutDevis.ACCEPTE, debutMois, finMois);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getMontantPaiementsMoisCourant() {
        LocalDate maintenant = LocalDate.now();
        LocalDate debutMois = maintenant.withDayOfMonth(1);
        LocalDate finMois = maintenant.withDayOfMonth(maintenant.lengthOfMonth());
        BigDecimal total = paiementRepository.sumMontantValideByPeriode(debutMois, finMois);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Materiel> getDerniersMateriels() {
        return materielRepository.findTop5ByOrderByIdDesc();
    }

    public List<Reparation> getDernieresReparations() {
        return reparationRepository.findTop5ByOrderByIdDesc();
    }

    public List<Plainte> getDernieresPlaintes() {
        return plainteRepository.findTop5ByOrderByIdDesc();
    }

    public List<HistoriqueMateriel> getDernieresOperations() {
        return historiqueMaterielRepository.findTop8ByOrderByDateModificationDesc();
    }
}
