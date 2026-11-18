package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroPaiement;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull(message = "La réparation concernée est obligatoire.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reparation_id", nullable = false)
    private Reparation reparation;

    @NotNull(message = "La date du paiement est obligatoire.")
    @Column(nullable = false)
    private LocalDate datePaiement = LocalDate.now();

    @NotNull(message = "Le montant est obligatoire.")
    @DecimalMin(value = "1.00", message = "Le montant du paiement doit être strictement positif.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ModePaiement modePaiement;

    @Column(length = 120)
    private String reference;

    @Column(length = 500)
    private String observation;

    @Column(nullable = false)
    private boolean annule = false;

    @Column(length = 400)
    private String motifAnnulation;

    private LocalDateTime dateAnnulation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateurEnregistrement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPaiement() {
        return numeroPaiement;
    }

    public void setNumeroPaiement(String numeroPaiement) {
        this.numeroPaiement = numeroPaiement;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Reparation getReparation() {
        return reparation;
    }

    public void setReparation(Reparation reparation) {
        this.reparation = reparation;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public boolean isAnnule() {
        return annule;
    }

    public void setAnnule(boolean annule) {
        this.annule = annule;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public LocalDateTime getDateAnnulation() {
        return dateAnnulation;
    }

    public void setDateAnnulation(LocalDateTime dateAnnulation) {
        this.dateAnnulation = dateAnnulation;
    }

    public Utilisateur getUtilisateurEnregistrement() {
        return utilisateurEnregistrement;
    }

    public void setUtilisateurEnregistrement(Utilisateur utilisateurEnregistrement) {
        this.utilisateurEnregistrement = utilisateurEnregistrement;
    }
}
