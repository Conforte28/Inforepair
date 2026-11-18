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

        ParametresEntreprise params = parametresEntrepriseService.getParametres();
        params.setNomEntreprise("INFOREPAIR Bénin");
        params.setCommune("Cotonou");
        params.setAdresse("Quartier Haie Vive, Rue des Cocotiers");
        params.setTelephone("+229 01 97 12 34 56");
        params.setEmail("contact@inforepair.bj");
        parametresEntrepriseService.enregistrer(params);

        String hash = passwordEncoder.encode(demoPassword);

        Utilisateur admin = new Utilisateur();
        admin.setNomComplet("Kossi Dossou");
        admin.setEmail("admin@inforepair.bj");
        admin.setTelephone("+229 01 97 00 00 01");
        admin.setMotDePasse(hash);
        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        admin = utilisateurRepository.save(admin);

        Utilisateur agent = new Utilisateur();
        agent.setNomComplet("Mireille Agbossou");
        agent.setEmail("agent@inforepair.bj");
        agent.setTelephone("+229 01 96 00 00 02");
        agent.setMotDePasse(hash);
        agent.setRole(Role.AGENT);
        agent.setActif(true);
        agent = utilisateurRepository.save(agent);

        Utilisateur technicien = new Utilisateur();
        technicien.setNomComplet("Félix Houngbédji");
        technicien.setEmail("technicien@inforepair.bj");
        technicien.setTelephone("+229 01 95 00 00 03");
        technicien.setMotDePasse(hash);
        technicien.setRole(Role.TECHNICIEN);
        technicien.setActif(true);
        technicien = utilisateurRepository.save(technicien);

        Client c1 = creerClient("CLI-2026-0001", "Adjovi Mensah", "+229 01 97 45 12 10", "adjovi.mensah@exemple.bj", "Cotonou - Akpakpa", LocalDate.now().minusDays(20), "Client régulier");
        Client c2 = creerClient("CLI-2026-0002", "Sèna Kpadonou", "+229 01 96 33 21 08", "sena.kpadonou@exemple.bj", "Abomey-Calavi - Tankpè", LocalDate.now().minusDays(14), "Entreprise de comptabilité");
        Client c3 = creerClient("CLI-2026-0003", "Brice Tossou", "+229 01 94 11 88 22", null, "Porto-Novo - Ouando", LocalDate.now().minusDays(5), null);
        Client c4 = creerClient("CLI-2026-0004", "Chantal Gbèdo", "+229 01 95 77 44 19", "chantal.gbedo@exemple.bj", "Cotonou - Cadjèhoun", LocalDate.now().minusDays(2), "Préfère être contactée par téléphone");

        Materiel m1 = creerMateriel("MAT-2026-0001", c1, TypeMateriel.ORDINATEUR_PORTABLE, "HP", "EliteBook 840 G6", "5CG9214ABC", "Chargeur secteur", LocalDate.now().minusDays(15), "Écran cassé ou défectueux", "Coque en bon état général", EtatMateriel.REPARATION_TERMINEE);
        Materiel m2 = creerMateriel("MAT-2026-0002", c2, TypeMateriel.ORDINATEUR_BUREAU, "Dell", "OptiPlex 7080", "DL7080-991", "Câble d'alimentation", LocalDate.now().minusDays(10), "Problème d'alimentation", "Poussière interne importante", EtatMateriel.EN_REPARATION);
        Materiel m3 = creerMateriel("MAT-2026-0003", c3, TypeMateriel.IMPRIMANTE, "Epson", "EcoTank L3250", "EPS-3250-77", "Câble USB", LocalDate.now().minusDays(4), "Problème de connexion réseau", "Traces légères d'usage", EtatMateriel.DEVIS_EN_ATTENTE);
        Materiel m4 = creerMateriel("MAT-2026-0004", c4, TypeMateriel.ONDULEUR, "APC", "Back-UPS 1100VA", "APC-1100-42", "Aucun accessoire", LocalDate.now().minusDays(1), "Problème de batterie", "Boîtier intact", EtatMateriel.EN_ATTENTE_DIAGNOSTIC);

        enregistrerHistoriqueMateriel(m1, null, EtatMateriel.RECU, LocalDateTime.now().minusDays(15), agent, "Dépôt du matériel au comptoir.");
        enregistrerHistoriqueMateriel(m1, EtatMateriel.RECU, EtatMateriel.DEVIS_ACCEPTE, LocalDateTime.now().minusDays(14), agent, "Devis accepté par le client.");
        enregistrerHistoriqueMateriel(m1, EtatMateriel.DEVIS_ACCEPTE, EtatMateriel.EN_REPARATION, LocalDateTime.now().minusDays(13), technicien, "Remplacement de la dalle lancé.");
        enregistrerHistoriqueMateriel(m1, EtatMateriel.EN_REPARATION, EtatMateriel.REPARATION_TERMINEE, LocalDateTime.now().minusDays(11), technicien, "Tests d'affichage validés.");

        enregistrerHistoriqueMateriel(m2, null, EtatMateriel.RECU, LocalDateTime.now().minusDays(10), agent, "Réception de l'unité centrale.");
        enregistrerHistoriqueMateriel(m2, EtatMateriel.RECU, EtatMateriel.DEVIS_ACCEPTE, LocalDateTime.now().minusDays(9), agent, "Validation du devis par l'entreprise cliente.");
        enregistrerHistoriqueMateriel(m2, EtatMateriel.DEVIS_ACCEPTE, EtatMateriel.EN_REPARATION, LocalDateTime.now().minusDays(8), technicien, "Intervention en cours sur le bloc d'alimentation.");

        enregistrerHistoriqueMateriel(m3, null, EtatMateriel.RECU, LocalDateTime.now().minusDays(4), agent, "Imprimante reçue pour diagnostic Wi-Fi.");
        enregistrerHistoriqueMateriel(m3, EtatMateriel.RECU, EtatMateriel.DEVIS_EN_ATTENTE, LocalDateTime.now().minusDays(3), agent, "Devis transmis au client.");

        enregistrerHistoriqueMateriel(m4, null, EtatMateriel.EN_ATTENTE_DIAGNOSTIC, LocalDateTime.now().minusDays(1), agent, "En attente de test de batterie.");

        Devis d1 = new Devis();
        d1.setNumeroDevis("DEV-2026-0001");
        d1.setMateriel(m1);
        d1.setClient(c1);
        d1.setDateCreation(LocalDate.now().minusDays(14));
        d1.setDateValidite(LocalDate.now().plusDays(1));
        d1.setDateDecision(LocalDate.now().minusDays(14));
        d1.setDiagnosticTechnique("Dalle LCD 14 pouces FHD fissurée suite à un choc. Carte mère fonctionnelle.");
        d1.setCoutMainOeuvre(new BigDecimal("10000.00"));
        d1.setCoutPieces(BigDecimal.ZERO);
        d1.setStatut(StatutDevis.ACCEPTE);
        LigneDevis l1 = new LigneDevis();
        l1.setDevis(d1);
        l1.setDesignation("Écran LCD 14 pouces FHD IPS");
        l1.setQuantite(1);
        l1.setPrixUnitaire(new BigDecimal("55000.00"));
        l1.setSousTotal(new BigDecimal("55000.00"));
        d1.getLignes().add(l1);
        d1.setMontantTotal(new BigDecimal("65000.00"));
        d1 = devisRepository.save(d1);

        Devis d2 = new Devis();
        d2.setNumeroDevis("DEV-2026-0002");
        d2.setMateriel(m2);
        d2.setClient(c2);
        d2.setDateCreation(LocalDate.now().minusDays(9));
        d2.setDateValidite(LocalDate.now().plusDays(6));
        d2.setDateDecision(LocalDate.now().minusDays(9));
        d2.setDiagnosticTechnique("Bloc d'alimentation défectueux et dépoussiérage complet requis.");
        d2.setCoutMainOeuvre(new BigDecimal("7500.00"));
        d2.setCoutPieces(BigDecimal.ZERO);
        d2.setStatut(StatutDevis.ACCEPTE);
        LigneDevis l2 = new LigneDevis();
        l2.setDevis(d2);
        l2.setDesignation("Bloc d'alimentation Dell 260W");
        l2.setQuantite(1);
        l2.setPrixUnitaire(new BigDecimal("27500.00"));
        l2.setSousTotal(new BigDecimal("27500.00"));
        d2.getLignes().add(l2);
        d2.setMontantTotal(new BigDecimal("35000.00"));
        d2 = devisRepository.save(d2);

        Devis d3 = new Devis();
        d3.setNumeroDevis("DEV-2026-0003");
        d3.setMateriel(m3);
        d3.setClient(c3);
        d3.setDateCreation(LocalDate.now().minusDays(3));
        d3.setDateValidite(LocalDate.now().plusDays(12));
        d3.setDiagnosticTechnique("Module Wi-Fi instable et mise à jour micrologiciel nécessaire.");
        d3.setCoutMainOeuvre(new BigDecimal("8000.00"));
        d3.setCoutPieces(new BigDecimal("12000.00"));
        d3.setStatut(StatutDevis.EN_ATTENTE);
        LigneDevis l3 = new LigneDevis();
        l3.setDevis(d3);
        l3.setDesignation("Reconfiguration réseau et maintenance têtes");
        l3.setQuantite(1);
        l3.setPrixUnitaire(new BigDecimal("5000.00"));
        l3.setSousTotal(new BigDecimal("5000.00"));
        d3.getLignes().add(l3);
        d3.setMontantTotal(new BigDecimal("25000.00"));
        devisRepository.save(d3);

        Reparation r1 = new Reparation();
        r1.setNumeroReparation("REP-2026-0001");
        r1.setDevis(d1);
        r1.setMateriel(m1);
        r1.setClient(c1);
        r1.setTechnicien(technicien);
        r1.setDateDebut(LocalDate.now().minusDays(13));
        r1.setDateFin(LocalDate.now().minusDays(11));
        r1.setDescriptionTravail("Démontage du cadre d'écran, remplacement de la dalle 14 pouces et vérification du rétroéclairage.");
        r1.setPiecesUtilisees("Dalle LCD 14 pouces FHD IPS");
        r1.setCoutPieces(new BigDecimal("55000.00"));
        r1.setCoutMainOeuvre(new BigDecimal("10000.00"));
        r1.setStatut(StatutReparation.TERMINEE);
        r1.setObservation("Tests vidéo concluants.");
        r1 = reparationRepository.save(r1);

        Reparation r2 = new Reparation();
        r2.setNumeroReparation("REP-2026-0002");
        r2.setDevis(d2);
        r2.setMateriel(m2);
        r2.setClient(c2);
        r2.setTechnicien(technicien);
        r2.setDateDebut(LocalDate.now().minusDays(8));
        r2.setDescriptionTravail("Dépoussiérage complet et test de tension en cours.");
        r2.setPiecesUtilisees("Bloc d'alimentation Dell 260W");
        r2.setCoutPieces(new BigDecimal("27500.00"));
        r2.setCoutMainOeuvre(new BigDecimal("7500.00"));
        r2.setStatut(StatutReparation.EN_COURS);
        r2.setObservation("Test de stabilité sur 24 heures en cours.");
        r2 = reparationRepository.save(r2);

        Intervention i1 = new Intervention();
        i1.setReparation(r1);
        i1.setDateIntervention(LocalDateTime.now().minusDays(12));
        i1.setTechnicien(technicien);
        i1.setDescriptionTravail("Remplacement de la dalle cassée et test de charnière.");
        i1.setPiecesUtilisees("Dalle LCD 14 pouces FHD IPS");
        i1.setObservation("Aucune anomalie détectée.");
        interventionRepository.save(i1);

        Intervention i2 = new Intervention();
        i2.setReparation(r2);
        i2.setDateIntervention(LocalDateTime.now().minusDays(7));
        i2.setTechnicien(technicien);
        i2.setDescriptionTravail("Extraction de l'alimentation défectueuse et nettoyage de la carte mère.");
        i2.setPiecesUtilisees("Pâte thermique, bombe à air");
        i2.setObservation("Installation du nouveau bloc prévue.");
        interventionRepository.save(i2);

        Paiement p1 = new Paiement();
        p1.setNumeroPaiement("PAI-2026-0001");
        p1.setClient(c1);
        p1.setReparation(r1);
        p1.setDatePaiement(LocalDate.now().minusDays(11));
        p1.setMontant(new BigDecimal("65000.00"));
        p1.setModePaiement(ModePaiement.MOBILE_MONEY);
        p1.setReference("MTN-MM-884120");
        p1.setObservation("Règlement intégral de la réparation.");
        p1.setUtilisateurEnregistrement(agent);
        paiementRepository.save(p1);

        Paiement p2 = new Paiement();
        p2.setNumeroPaiement("PAI-2026-0002");
        p2.setClient(c2);
        p2.setReparation(r2);
        p2.setDatePaiement(LocalDate.now().minusDays(8));
        p2.setMontant(new BigDecimal("15000.00"));
        p2.setModePaiement(ModePaiement.ESPECES);
        p2.setReference("RECU-0024");
        p2.setObservation("Acompte versé au démarrage.");
        p2.setUtilisateurEnregistrement(agent);
        paiementRepository.save(p2);

        Plainte pl1 = new Plainte();
        pl1.setNumeroPlainte("PLA-2026-0001");
        pl1.setClient(c2);
        pl1.setMateriel(m2);
        pl1.setReparation(r2);
        pl1.setDateDepot(LocalDate.now().minusDays(2));
        pl1.setTypePlainte(TypePlainte.RETARD_REPARATION);
        pl1.setObjet("Délai de livraison dépassé de deux jours");
        pl1.setDescription("Le client souhaite récupérer l'unité centrale rapidement pour la comptabilité.");
        pl1.setResponsable(technicien);
        pl1.setStatut(StatutPlainte.EN_COURS_TRAITEMENT);
        pl1.setObservation("Priorité accordée à la finalisation des tests.");
        pl1 = plainteRepository.save(pl1);

        HistoriquePlainte hp1 = new HistoriquePlainte();
        hp1.setPlainte(pl1);
        hp1.setAncienStatut(null);
        hp1.setNouveauStatut(StatutPlainte.NOUVELLE);
        hp1.setDateModification(LocalDateTime.now().minusDays(2));
        hp1.setUtilisateur(agent);
        hp1.setActionRealisee("Enregistrement de la plainte suite à l'appel du client.");
        historiquePlainteRepository.save(hp1);

        HistoriquePlainte hp2 = new HistoriquePlainte();
        hp2.setPlainte(pl1);
        hp2.setAncienStatut(StatutPlainte.NOUVELLE);
        hp2.setNouveauStatut(StatutPlainte.EN_COURS_TRAITEMENT);
        hp2.setDateModification(LocalDateTime.now().minusDays(1));
        hp2.setUtilisateur(admin);
        hp2.setActionRealisee("Affectation au technicien pour finalisation prioritaire.");
        historiquePlainteRepository.save(hp2);
    }

    private Client creerClient(String code, String nom, String tel, String email, String adresse, LocalDate date, String obs) {
        Client c = new Client();
        c.setCodeClient(code);
        c.setNomComplet(nom);
        c.setTelephone(tel);
        c.setEmail(email);
        c.setAdresseCommune(adresse);
        c.setDateEnregistrement(date);
        c.setObservation(obs);
        c.setActif(true);
        return clientRepository.save(c);
    }

    private Materiel creerMateriel(String dossier, Client client, TypeMateriel type, String marque, String modele,
                                   String serie, String accessoires, LocalDate dateDepot, String probleme,
                                   String etatPhysique, EtatMateriel etat) {
        Materiel m = new Materiel();
        m.setNumeroDossier(dossier);
        m.setClient(client);
        m.setTypeMateriel(type);
        m.setMarque(marque);
        m.setModele(modele);
        m.setNumeroSerie(serie);
        m.setAccessoiresDeposes(accessoires);
        m.setDateDepot(dateDepot);
        m.setDescriptionProbleme(probleme);
        m.setEtatPhysique(etatPhysique);
        m.setEtatActuel(etat);
        return materielRepository.save(m);
    }

    private void enregistrerHistoriqueMateriel(Materiel m, EtatMateriel ancien, EtatMateriel nouvel,
                                               LocalDateTime date, Utilisateur user, String obs) {
        HistoriqueMateriel h = new HistoriqueMateriel();
        h.setMateriel(m);
        h.setAncienEtat(ancien);
        h.setNouvelEtat(nouvel);
        h.setDateModification(date);
        h.setUtilisateur(user);
        h.setObservation(obs);
        historiqueMaterielRepository.save(h);
    }
}
