package bj.inforepair.config;

import bj.inforepair.entity.*;
import bj.inforepair.repository.*;
import bj.inforepair.service.ParametresEntrepriseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final MaterielRepository materielRepository;
    private final HistoriqueMaterielRepository historiqueMaterielRepository;
    private final DevisRepository devisRepository;
    private final ReparationRepository reparationRepository;
    private final InterventionRepository interventionRepository;
    private final PaiementRepository paiementRepository;
    private final PlainteRepository plainteRepository;
    private final HistoriquePlainteRepository historiquePlainteRepository;
    private final ParametresEntrepriseService parametresEntrepriseService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.mode:false}")
    private boolean demoMode;

    @Value("${app.demo.password:}")
    private String demoPassword;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           ClientRepository clientRepository,
                           MaterielRepository materielRepository,
                           HistoriqueMaterielRepository historiqueMaterielRepository,
                           DevisRepository devisRepository,
                           ReparationRepository reparationRepository,
                           InterventionRepository interventionRepository,
                           PaiementRepository paiementRepository,
                           PlainteRepository plainteRepository,
                           HistoriquePlainteRepository historiquePlainteRepository,
                           ParametresEntrepriseService parametresEntrepriseService,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.materielRepository = materielRepository;
        this.historiqueMaterielRepository = historiqueMaterielRepository;
        this.devisRepository = devisRepository;
        this.reparationRepository = reparationRepository;
        this.interventionRepository = interventionRepository;
        this.paiementRepository = paiementRepository;
        this.plainteRepository = plainteRepository;
        this.historiquePlainteRepository = historiquePlainteRepository;
        this.parametresEntrepriseService = parametresEntrepriseService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        parametresEntrepriseService.getParametres();

        if (!demoMode || demoPassword == null || demoPassword.trim().isEmpty()) {
            return;
        }
        if (utilisateurRepository.count() > 0) {
            return;
        }

        String hash = passwordEncoder.encode(demoPassword);

        Utilisateur admin = new Utilisateur();
        admin.setNomComplet("Administrateur");
        admin.setEmail("admin@inforepair.bj");
        admin.setTelephone("+229 01 00 00 00 01");
        admin.setMotDePasse(hash);
        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        admin = utilisateurRepository.save(admin);

        Utilisateur agent = new Utilisateur();
        agent.setNomComplet("Agent d'accueil");
        agent.setEmail("agent@inforepair.bj");
        agent.setTelephone("+229 01 00 00 00 02");
        agent.setMotDePasse(hash);
        agent.setRole(Role.AGENT);
        agent.setActif(true);
        agent = utilisateurRepository.save(agent);

        Utilisateur technicien = new Utilisateur();
        technicien.setNomComplet("Technicien");
        technicien.setEmail("technicien@inforepair.bj");
        technicien.setTelephone("+229 01 00 00 00 03");
        technicien.setMotDePasse(hash);
        technicien.setRole(Role.TECHNICIEN);
        technicien.setActif(true);
        technicien = utilisateurRepository.save(technicien);

        Client clientExemple = new Client();
        clientExemple.setCodeClient("CLI-2026-0001");
        clientExemple.setNomComplet("Client Exemple (Koffi Mensah)");
        clientExemple.setTelephone("+229 01 97 00 00 00");
        clientExemple.setEmail("client@exemple.bj");
        clientExemple.setAdresseCommune("Cotonou - Akpakpa");
        clientExemple.setDateEnregistrement(LocalDate.now());
        clientExemple.setObservation("Fiche exemple montrant le format d'enregistrement d'un client.");
        clientExemple.setActif(true);
        clientExemple = clientRepository.save(clientExemple);

        Materiel materielExemple = new Materiel();
        materielExemple.setNumeroDossier("MAT-2026-0001");
        materielExemple.setClient(clientExemple);
        materielExemple.setTypeMateriel(TypeMateriel.ORDINATEUR_PORTABLE);
        materielExemple.setMarque("HP");
        materielExemple.setModele("EliteBook 840 G6");
        materielExemple.setNumeroSerie("SN-EXEMPLE-001");
        materielExemple.setAccessoiresDeposes("Chargeur secteur");
        materielExemple.setDateDepot(LocalDate.now());
        materielExemple.setDescriptionProbleme("Problème d'alimentation");
        materielExemple.setEtatPhysique("Bon état général");
        materielExemple.setObservation("Exemple de matériel enregistré.");
        materielExemple.setEtatActuel(EtatMateriel.EN_REPARATION);
        materielExemple = materielRepository.save(materielExemple);

        HistoriqueMateriel histMateriel = new HistoriqueMateriel();
        histMateriel.setMateriel(materielExemple);
        histMateriel.setAncienEtat(EtatMateriel.DEVIS_ACCEPTE);
        histMateriel.setNouvelEtat(EtatMateriel.EN_REPARATION);
        histMateriel.setDateModification(LocalDateTime.now());
        histMateriel.setUtilisateur(agent);
        histMateriel.setObservation("Exemple d'historique de suivi du matériel.");
        historiqueMaterielRepository.save(histMateriel);

        Devis devisExemple = new Devis();
        devisExemple.setNumeroDevis("DEV-2026-0001");
        devisExemple.setMateriel(materielExemple);
        devisExemple.setClient(clientExemple);
        devisExemple.setDateCreation(LocalDate.now());
        devisExemple.setDateValidite(LocalDate.now().plusDays(15));
        devisExemple.setDateDecision(LocalDate.now());
        devisExemple.setDiagnosticTechnique("Exemple de diagnostic : défaut du circuit d'alimentation interne.");
        devisExemple.setCoutMainOeuvre(new BigDecimal("10000.00"));
        devisExemple.setCoutPieces(BigDecimal.ZERO);
        devisExemple.setStatut(StatutDevis.ACCEPTE);

        LigneDevis ligneExemple = new LigneDevis();
        ligneExemple.setDevis(devisExemple);
        ligneExemple.setDesignation("Remplacement connecteur d'alimentation");
        ligneExemple.setQuantite(1);
        ligneExemple.setPrixUnitaire(new BigDecimal("15000.00"));
        ligneExemple.setSousTotal(new BigDecimal("15000.00"));
        devisExemple.getLignes().add(ligneExemple);
        devisExemple.setMontantTotal(new BigDecimal("25000.00"));
        devisExemple = devisRepository.save(devisExemple);

        Reparation reparationExemple = new Reparation();
        reparationExemple.setNumeroReparation("REP-2026-0001");
        reparationExemple.setDevis(devisExemple);
        reparationExemple.setMateriel(materielExemple);
        reparationExemple.setClient(clientExemple);
        reparationExemple.setTechnicien(technicien);
        reparationExemple.setDateDebut(LocalDate.now());
        reparationExemple.setDescriptionTravail("Exemple de réparation : démontage et remplacement du composant.");
        reparationExemple.setPiecesUtilisees("Connecteur d'alimentation DC");
        reparationExemple.setCoutPieces(new BigDecimal("15000.00"));
        reparationExemple.setCoutMainOeuvre(new BigDecimal("10000.00"));
        reparationExemple.setStatut(StatutReparation.EN_COURS);
        reparationExemple.setObservation("Exemple de fiche de réparation en cours.");
        reparationExemple = reparationRepository.save(reparationExemple);

        Intervention interventionExemple = new Intervention();
        interventionExemple.setReparation(reparationExemple);
        interventionExemple.setDateIntervention(LocalDateTime.now());
        interventionExemple.setTechnicien(technicien);
        interventionExemple.setDescriptionTravail("Exemple d'intervention : contrôle de tension sur la carte mère.");
        interventionExemple.setPiecesUtilisees("Connecteur d'alimentation DC");
        interventionExemple.setObservation("Test en cours.");
        interventionRepository.save(interventionExemple);

        Paiement paiementExemple = new Paiement();
        paiementExemple.setNumeroPaiement("PAI-2026-0001");
        paiementExemple.setClient(clientExemple);
        paiementExemple.setReparation(reparationExemple);
        paiementExemple.setDatePaiement(LocalDate.now());
        paiementExemple.setMontant(new BigDecimal("10000.00"));
        paiementExemple.setModePaiement(ModePaiement.ESPECES);
        paiementExemple.setReference("RECU-EXEMPLE-01");
        paiementExemple.setObservation("Exemple d'acompte enregistré.");
        paiementExemple.setUtilisateurEnregistrement(agent);
        paiementRepository.save(paiementExemple);

        Plainte plainteExemple = new Plainte();
        plainteExemple.setNumeroPlainte("PLA-2026-0001");
        plainteExemple.setClient(clientExemple);
        plainteExemple.setMateriel(materielExemple);
        plainteExemple.setReparation(reparationExemple);
        plainteExemple.setDateDepot(LocalDate.now());
        plainteExemple.setTypePlainte(TypePlainte.RETARD_REPARATION);
        plainteExemple.setObjet("Exemple de réclamation client");
        plainteExemple.setDescription("Exemple de description détaillée d'une plainte client.");
        plainteExemple.setResponsable(admin);
        plainteExemple.setStatut(StatutPlainte.NOUVELLE);
        plainteExemple.setObservation("Exemple à traiter ou clôturer.");
        plainteExemple = plainteRepository.save(plainteExemple);

        HistoriquePlainte histPlainte = new HistoriquePlainte();
        histPlainte.setPlainte(plainteExemple);
        histPlainte.setAncienStatut(null);
        histPlainte.setNouveauStatut(StatutPlainte.NOUVELLE);
        histPlainte.setDateModification(LocalDateTime.now());
        histPlainte.setUtilisateur(agent);
        histPlainte.setActionRealisee("Enregistrement initial de la plainte exemple.");
        historiquePlainteRepository.save(histPlainte);
    }
}
