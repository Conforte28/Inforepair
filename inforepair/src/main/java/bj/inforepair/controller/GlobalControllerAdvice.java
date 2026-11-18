package bj.inforepair.controller;

import bj.inforepair.entity.ParametresEntreprise;
import bj.inforepair.entity.Utilisateur;
import bj.inforepair.service.ParametresEntrepriseService;
import bj.inforepair.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final ParametresEntrepriseService parametresEntrepriseService;
    private final UtilisateurService utilisateurService;

    public GlobalControllerAdvice(ParametresEntrepriseService parametresEntrepriseService,
                                  UtilisateurService utilisateurService) {
        this.parametresEntrepriseService = parametresEntrepriseService;
        this.utilisateurService = utilisateurService;
    }

    @ModelAttribute("entreprise")
    public ParametresEntreprise getEntreprise() {
        return parametresEntrepriseService.getParametres();
    }

    @ModelAttribute("utilisateurConnecte")
    public Utilisateur getUtilisateurConnecte(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
    }
}
