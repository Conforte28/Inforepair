package bj.inforepair.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_materiels")
public class HistoriqueMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private EtatMateriel ancienEtat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EtatMateriel nouvelEtat;

    @Column(nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(length = 500)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public EtatMateriel getAncienEtat() {
        return ancienEtat;
    }

    public void setAncienEtat(EtatMateriel ancienEtat) {
        this.ancienEtat = ancienEtat;
    }

    public EtatMateriel getNouvelEtat() {
        return nouvelEtat;
    }

    public void setNouvelEtat(EtatMateriel nouvelEtat) {
        this.nouvelEtat = nouvelEtat;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
