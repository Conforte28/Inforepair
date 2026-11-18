package bj.inforepair.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codeClient;

    @NotBlank(message = "Le nom et prénom du client est obligatoire.")
    @Column(nullable = false, length = 150)
    private String nomComplet;

    @NotBlank(message = "Le numéro de téléphone est obligatoire.")
    @Column(nullable = false, length = 50)
    private String telephone;

    @Email(message = "L'adresse électronique n'est pas valide.")
    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String adresseCommune;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate dateEnregistrement = LocalDate.now();

    @Column(length = 500)
    private String observation;

    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresseCommune() {
        return adresseCommune;
    }

    public void setAdresseCommune(String adresseCommune) {
        this.adresseCommune = adresseCommune;
    }

    public LocalDate getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDate dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
