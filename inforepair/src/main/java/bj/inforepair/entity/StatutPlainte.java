package bj.inforepair.entity;

public enum StatutPlainte {
    NOUVELLE("Nouvelle"),
    EN_COURS_TRAITEMENT("En cours de traitement"),
    EN_ATTENTE_CLIENT("En attente du client"),
    RESOLUE("Résolue"),
    CLOTUREE("Clôturée");

    private final String libelle;

    StatutPlainte(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
