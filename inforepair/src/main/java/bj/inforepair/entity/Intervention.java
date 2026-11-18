package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reparation_id", nullable = false)
    private Reparation reparation;

    @NotNull(message = "La date de l'intervention est obligatoire.")
    @Column(nullable = false)
    private LocalDateTime dateIntervention = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technicien_id")
    private Utilisateur technicien;

    @NotBlank(message = "La description du travail effectué est obligatoire.")
    @Column(nullable = false, length = 1200)
    private String descriptionTravail;

    @Column(length = 600)
    private String piecesUtilisees;

    @Column(length = 600)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reparation getReparation() {
        return reparation;
    }

    public void setReparation(Reparation reparation) {
        this.reparation = reparation;
    }

    public LocalDateTime getDateIntervention() {
        return dateIntervention;
    }

    public void setDateIntervention(LocalDateTime dateIntervention) {
        this.dateIntervention = dateIntervention;
    }

    public Utilisateur getTechnicien() {
        return technicien;
    }

    public void setTechnicien(Utilisateur technicien) {
        this.technicien = technicien;
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

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
