package bj.inforepair.entity;

public enum TypePlainte {
    RETARD_REPARATION("Retard de réparation"),
    PROBLEME_APRES_REPARATION("Problème après réparation"),
    MAUVAISE_QUALITE_SERVICE("Mauvaise qualité du service"),
    DESACCORD_MONTANT("Désaccord sur le montant facturé"),
    MATERIEL_ENDOMMAGE("Matériel endommagé"),
    PERTE_ACCESSOIRES("Perte d'accessoires"),
    AUTRE("Autre réclamation");

    private final String libelle;

    TypePlainte(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
