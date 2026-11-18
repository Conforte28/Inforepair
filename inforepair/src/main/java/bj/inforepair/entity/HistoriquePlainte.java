package bj.inforepair.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_plaintes")
public class HistoriquePlainte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plainte_id", nullable = false)
    private Plainte plainte;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private StatutPlainte ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutPlainte nouveauStatut;

    @Column(nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(length = 800)
    private String actionRealisee;

    @Column(length = 500)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plainte getPlainte() {
        return plainte;
    }

    public void setPlainte(Plainte plainte) {
        this.plainte = plainte;
    }

    public StatutPlainte getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(StatutPlainte ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public StatutPlainte getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutPlainte nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
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

    public String getActionRealisee() {
        return actionRealisee;
    }

    public void setActionRealisee(String actionRealisee) {
        this.actionRealisee = actionRealisee;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
