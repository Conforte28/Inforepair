package bj.inforepair.entity;

public enum StatutDevis {
    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé"),
    EXPIRE("Expiré");

    private final String libelle;

    StatutDevis(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
