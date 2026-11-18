package bj.inforepair.entity;

public enum EtatMateriel {
    RECU("Reçu"),
    EN_ATTENTE_DIAGNOSTIC("En attente de diagnostic"),
    DIAGNOSTIC_EFFECTUE("Diagnostic effectué"),
    DEVIS_EN_ATTENTE("Devis en attente"),
    DEVIS_ACCEPTE("Devis accepté"),
    DEVIS_REFUSE("Devis refusé"),
    EN_REPARATION("En réparation"),
    EN_ATTENTE_PIECES("En attente de pièces"),
    REPARATION_TERMINEE("Réparation terminée"),
    PRET_A_ETRE_RETIRE("Prêt à être retiré"),
    REMIS_AU_CLIENT("Remis au client"),
    IRREPARABLE("Irréparable");

    private final String libelle;

    EtatMateriel(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
