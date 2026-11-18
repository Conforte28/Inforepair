package bj.inforepair.entity;

public enum StatutReparation {
    NON_COMMENCEE("Non commencée"),
    EN_COURS("En cours"),
    EN_ATTENTE_PIECES("En attente de pièces"),
    TERMINEE("Terminée"),
    CONTROLE_QUALITE("Contrôle qualité"),
    PRETE_A_ETRE_RETIREE("Prête à être retirée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutReparation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
