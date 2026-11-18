package bj.inforepair.entity;

public enum TypeMateriel {
    ORDINATEUR_PORTABLE("Ordinateur portable"),
    ORDINATEUR_BUREAU("Ordinateur de bureau"),
    IMPRIMANTE("Imprimante"),
    ECRAN("Écran"),
    CLAVIER("Clavier"),
    SOURIS("Souris"),
    ONDULEUR("Onduleur"),
    SERVEUR("Serveur"),
    ROUTEUR("Routeur"),
    SWITCH("Switch"),
    AUTRE("Autre matériel informatique");

    private final String libelle;

    TypeMateriel(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
