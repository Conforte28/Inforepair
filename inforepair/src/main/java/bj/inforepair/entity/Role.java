package bj.inforepair.entity;

public enum Role {
    ADMIN("Administrateur"),
    AGENT("Agent d'accueil"),
    TECHNICIEN("Technicien");

    private final String libelle;

    Role(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
