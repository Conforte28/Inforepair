package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "materiels")
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroDossier;

    @NotNull(message = "Le client propriétaire est obligatoire.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull(message = "Le type de matériel est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TypeMateriel typeMateriel;

    @NotBlank(message = "La marque est obligatoire.")
    @Column(nullable = false, length = 100)
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire.")
    @Column(nullable = false, length = 100)
    private String modele;

    @Column(length = 100)
    private String numeroSerie;

    @Column(length = 300)
    private String accessoiresDeposes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "La date de dépôt est obligatoire.")
    @Column(nullable = false)
    private LocalDate dateDepot = LocalDate.now();

    @NotBlank(message = "La description du problème signalé est obligatoire.")
    @Column(nullable = false, length = 1000)
    private String descriptionProbleme;

    @Column(length = 300)
    private String etatPhysique;

    @Column(nullable = false)
    private boolean sessionCodeFourni = false;

    @Column(length = 250)
    private String noteAccesSession;

    @Column(length = 500)
    private String observation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EtatMateriel etatActuel = EtatMateriel.RECU;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDossier() {
        return numeroDossier;
    }

    public void setNumeroDossier(String numeroDossier) {
        this.numeroDossier = numeroDossier;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public TypeMateriel getTypeMateriel() {
        return typeMateriel;
    }

    public void setTypeMateriel(TypeMateriel typeMateriel) {
        this.typeMateriel = typeMateriel;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getAccessoiresDeposes() {
        return accessoiresDeposes;
    }

    public void setAccessoiresDeposes(String accessoiresDeposes) {
        this.accessoiresDeposes = accessoiresDeposes;
    }

    public LocalDate getDateDepot() {
        return dateDepot;
    }

    public void setDateDepot(LocalDate dateDepot) {
        this.dateDepot = dateDepot;
    }

    public String getDescriptionProbleme() {
        return descriptionProbleme;
    }

    public void setDescriptionProbleme(String descriptionProbleme) {
        this.descriptionProbleme = descriptionProbleme;
    }

    public String getEtatPhysique() {
        return etatPhysique;
    }

    public void setEtatPhysique(String etatPhysique) {
        this.etatPhysique = etatPhysique;
    }

    public boolean isSessionCodeFourni() {
        return sessionCodeFourni;
    }

    public void setSessionCodeFourni(boolean sessionCodeFourni) {
        this.sessionCodeFourni = sessionCodeFourni;
    }

    public String getNoteAccesSession() {
        return noteAccesSession;
    }

    public void setNoteAccesSession(String noteAccesSession) {
        this.noteAccesSession = noteAccesSession;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public EtatMateriel getEtatActuel() {
        return etatActuel;
    }

    public void setEtatActuel(EtatMateriel etatActuel) {
        this.etatActuel = etatActuel;
    }
}
