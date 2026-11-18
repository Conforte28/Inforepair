package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroDevis;

    @NotNull(message = "Le matériel concerné est obligatoire.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "La date de création est obligatoire.")
    @Column(nullable = false)
    private LocalDate dateCreation = LocalDate.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "La date de validité est obligatoire.")
    @Column(nullable = false)
    private LocalDate dateValidite = LocalDate.now().plusDays(15);

    private LocalDate dateDecision;

    @NotBlank(message = "Le diagnostic technique est obligatoire.")
    @Column(nullable = false, length = 1500)
    private String diagnosticTechnique;

    @NotNull(message = "Le coût de la main-d'œuvre est obligatoire.")
    @DecimalMin(value = "0.00", message = "Le coût de la main-d'œuvre ne peut pas être négatif.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coutMainOeuvre = BigDecimal.ZERO;

    @NotNull(message = "Le coût des pièces est obligatoire.")
    @DecimalMin(value = "0.00", message = "Le coût des pièces ne peut pas être négatif.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coutPieces = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(length = 600)
    private String observation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutDevis statut = StatutDevis.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "devis_parent_id")
    private Devis devisParent;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<LigneDevis> lignes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDevis() {
        return numeroDevis;
    }

    public void setNumeroDevis(String numeroDevis) {
        this.numeroDevis = numeroDevis;
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

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateValidite() {
        return dateValidite;
    }

    public void setDateValidite(LocalDate dateValidite) {
        this.dateValidite = dateValidite;
    }

    public LocalDate getDateDecision() {
        return dateDecision;
    }

    public void setDateDecision(LocalDate dateDecision) {
        this.dateDecision = dateDecision;
    }

    public String getDiagnosticTechnique() {
        return diagnosticTechnique;
    }

    public void setDiagnosticTechnique(String diagnosticTechnique) {
        this.diagnosticTechnique = diagnosticTechnique;
    }

    public BigDecimal getCoutMainOeuvre() {
        return coutMainOeuvre;
    }

    public void setCoutMainOeuvre(BigDecimal coutMainOeuvre) {
        this.coutMainOeuvre = coutMainOeuvre;
    }

    public BigDecimal getCoutPieces() {
        return coutPieces;
    }

    public void setCoutPieces(BigDecimal coutPieces) {
        this.coutPieces = coutPieces;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public StatutDevis getStatut() {
        return statut;
    }

    public void setStatut(StatutDevis statut) {
        this.statut = statut;
    }

    public Devis getDevisParent() {
        return devisParent;
    }

    public void setDevisParent(Devis devisParent) {
        this.devisParent = devisParent;
    }

    public List<LigneDevis> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneDevis> lignes) {
        this.lignes = lignes;
    }

    public BigDecimal getTotalPrestations() {
        BigDecimal total = BigDecimal.ZERO;
        if (lignes != null) {
            for (LigneDevis ligne : lignes) {
                if (ligne.getSousTotal() != null) {
                    total = total.add(ligne.getSousTotal());
                }
            }
        }
        return total;
    }
}
