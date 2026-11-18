package bj.inforepair.controller;

import bj.inforepair.entity.ParametresEntreprise;
import bj.inforepair.entity.Utilisateur;
import bj.inforepair.service.ParametresEntrepriseService;
import bj.inforepair.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parametres")
public class ParametresController {

    private final ParametresEntrepriseService parametresService;
    private final UtilisateurService utilisateurService;

    public ParametresController(ParametresEntrepriseService parametresService,
                                UtilisateurService utilisateurService) {
        this.parametresService = parametresService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("menuActif", "parametres");
        model.addAttribute("parametres", parametresService.getParametres());
        return "parametres/index";
    }

    @PostMapping("/entreprise")
    public String enregistrerEntreprise(@Valid @ModelAttribute("parametres") ParametresEntreprise parametres,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("menuActif", "parametres");
            return "parametres/index";
        }
        parametresService.enregistrer(parametres);
        redirectAttributes.addFlashAttribute("succes", "Paramètres de l'entreprise enregistrés.");
        return "redirect:/parametres";
    }

    @PostMapping("/mot-de-passe")
    public String changerMotDePasse(@RequestParam String motDePasseActuel,
                                    @RequestParam String nouveauMotDePasse,
                                    @RequestParam String confirmationMotDePasse,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
            redirectAttributes.addFlashAttribute("erreur", "La confirmation du mot de passe ne correspond pas.");
            return "redirect:/parametres";
        }
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElseThrow();
            utilisateurService.changerPropreMotDePasse(utilisateur, motDePasseActuel, nouveauMotDePasse);
            redirectAttributes.addFlashAttribute("succes", "Votre mot de passe a été modifié.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/parametres";
    }
}
