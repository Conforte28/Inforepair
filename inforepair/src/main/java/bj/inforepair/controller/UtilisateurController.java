package bj.inforepair.controller;

import bj.inforepair.entity.Role;
import bj.inforepair.entity.Utilisateur;
import bj.inforepair.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("menuActif", "utilisateurs");
        model.addAttribute("utilisateurs", utilisateurService.listerTous());
        return "utilisateurs/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("menuActif", "utilisateurs");
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("roles", Role.values());
        return "utilisateurs/formulaire";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("menuActif", "utilisateurs");
        model.addAttribute("utilisateur", utilisateurService.trouverParId(id));
        model.addAttribute("roles", Role.values());
        return "utilisateurs/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
                              BindingResult result,
                              @RequestParam(required = false) String motDePasseClair,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("menuActif", "utilisateurs");
            model.addAttribute("roles", Role.values());
            return "utilisateurs/formulaire";
        }
        try {
            if (utilisateur.getId() == null) {
                utilisateurService.creer(utilisateur, motDePasseClair);
                redirectAttributes.addFlashAttribute("succes", "Compte utilisateur créé.");
            } else {
                utilisateurService.modifier(utilisateur.getId(), utilisateur);
                redirectAttributes.addFlashAttribute("succes", "Compte utilisateur mis à jour.");
            }
            return "redirect:/utilisateurs";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("menuActif", "utilisateurs");
            model.addAttribute("roles", Role.values());
            model.addAttribute("erreur", e.getMessage());
            return "utilisateurs/formulaire";
        }
    }

    @PostMapping("/{id}/basculer-actif")
    public String basculerActif(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.basculerActif(id);
            redirectAttributes.addFlashAttribute("succes", "Statut du compte mis à jour.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/mot-de-passe")
    public String changerMotDePasse(@PathVariable Long id,
                                    @RequestParam String nouveauMotDePasse,
                                    RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.changerMotDePasse(id, nouveauMotDePasse);
            redirectAttributes.addFlashAttribute("succes", "Mot de passe modifié avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs/" + id + "/modifier";
    }
}
