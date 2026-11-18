package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_devis")
public class LigneDevis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "devis_id", nullable = false)
    private Devis devis;

    @NotBlank(message = "La désignation de la prestation est obligatoire.")
    @Column(nullable = false, length = 250)
    private String designation;

    @NotNull
    @Min(value = 1, message = "La quantité doit être supérieure ou égale à 1.")
    @Column(nullable = false)
    private Integer quantite = 1;

    @NotNull
    @DecimalMin(value = "0.00", message = "Le prix unitaire ne peut pas être négatif.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sousTotal = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Devis getDevis() {
        return devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(BigDecimal sousTotal) {
        this.sousTotal = sousTotal;
    }
}
