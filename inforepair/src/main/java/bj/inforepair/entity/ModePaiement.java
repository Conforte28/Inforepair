package bj.inforepair.entity;

public enum ModePaiement {
    ESPECES("Espèces"),
    MOBILE_MONEY("Mobile Money"),
    VIREMENT_BANCAIRE("Virement bancaire"),
    AUTRE("Autre");

    private final String libelle;

    ModePaiement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
