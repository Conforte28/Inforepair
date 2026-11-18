package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "plaintes")
public class Plainte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroPlainte;

    @NotNull(message = "Le client concerné est obligatoire.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "materiel_id")
    private Materiel materiel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reparation_id")
    private Reparation reparation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "La date de dépôt est obligatoire.")
    @Column(nullable = false)
    private LocalDate dateDepot = LocalDate.now();

    @NotNull(message = "Le type de plainte est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypePlainte typePlainte;

    @NotBlank(message = "L'objet de la plainte est obligatoire.")
    @Column(nullable = false, length = 200)
    private String objet;

    @NotBlank(message = "La description détaillée est obligatoire.")
    @Column(nullable = false, length = 1500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Column(length = 1200)
    private String solutionApportee;

    private LocalDate dateResolution;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutPlainte statut = StatutPlainte.NOUVELLE;

    @Column(length = 600)
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPlainte() {
        return numeroPlainte;
    }

    public void setNumeroPlainte(String numeroPlainte) {
        this.numeroPlainte = numeroPlainte;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Reparation getReparation() {
        return reparation;
    }

    public void setReparation(Reparation reparation) {
        this.reparation = reparation;
    }

    public LocalDate getDateDepot() {
        return dateDepot;
    }

    public void setDateDepot(LocalDate dateDepot) {
        this.dateDepot = dateDepot;
    }

    public TypePlainte getTypePlainte() {
        return typePlainte;
    }

    public void setTypePlainte(TypePlainte typePlainte) {
        this.typePlainte = typePlainte;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Utilisateur getResponsable() {
        return responsable;
    }

    public void setResponsable(Utilisateur responsable) {
        this.responsable = responsable;
    }

    public String getSolutionApportee() {
        return solutionApportee;
    }

    public void setSolutionApportee(String solutionApportee) {
        this.solutionApportee = solutionApportee;
    }

    public LocalDate getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDate dateResolution) {
        this.dateResolution = dateResolution;
    }

    public StatutPlainte getStatut() {
        return statut;
    }

    public void setStatut(StatutPlainte statut) {
        this.statut = statut;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
