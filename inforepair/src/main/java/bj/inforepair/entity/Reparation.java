package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reparations")
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroReparation;

    @NotNull(message = "Le devis accepté est obligatoire.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "devis_id", nullable = false)
    private Devis devis;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technicien_id")
    private Utilisateur technicien;

    @NotNull(message = "La date de début est obligatoire.")
    @Column(nullable = false)
    private LocalDate dateDebut = LocalDate.now();

    private LocalDate dateFin;

    @Column(length = 1500)
    private String descriptionTravail;

    @Column(length = 1000)
    private String piecesUtilisees;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coutPieces = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coutMainOeuvre = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutReparation statut = StatutReparation.NON_COMMENCEE;

    @Column(length = 800)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroReparation() {
        return numeroReparation;
    }

    public void setNumeroReparation(String numeroReparation) {
        this.numeroReparation = numeroReparation;
    }

    public Devis getDevis() {
        return devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Utilisateur getTechnicien() {
        return technicien;
    }

    public void setTechnicien(Utilisateur technicien) {
        this.technicien = technicien;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getDescriptionTravail() {
        return descriptionTravail;
    }

    public void setDescriptionTravail(String descriptionTravail) {
        this.descriptionTravail = descriptionTravail;
    }

    public String getPiecesUtilisees() {
        return piecesUtilisees;
    }

    public void setPiecesUtilisees(String piecesUtilisees) {
        this.piecesUtilisees = piecesUtilisees;
    }

    public BigDecimal getCoutPieces() {
        return coutPieces;
    }

    public void setCoutPieces(BigDecimal coutPieces) {
        this.coutPieces = coutPieces;
    }

    public BigDecimal getCoutMainOeuvre() {
        return coutMainOeuvre;
    }

    public void setCoutMainOeuvre(BigDecimal coutMainOeuvre) {
        this.coutMainOeuvre = coutMainOeuvre;
    }

    public StatutReparation getStatut() {
        return statut;
    }

    public void setStatut(StatutReparation statut) {
        this.statut = statut;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
