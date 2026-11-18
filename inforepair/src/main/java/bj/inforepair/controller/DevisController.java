package bj.inforepair.controller;

import bj.inforepair.entity.*;
import bj.inforepair.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/devis")
public class DevisController {

    private final DevisService devisService;
    private final MaterielService materielService;
    private final UtilisateurService utilisateurService;

    public DevisController(DevisService devisService,
                           MaterielService materielService,
                           UtilisateurService utilisateurService) {
        this.devisService = devisService;
        this.materielService = materielService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) StatutDevis statut,
                        Authentication authentication,
                        Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        model.addAttribute("menuActif", "devis");
        model.addAttribute("devisListe", devisService.lister(statut, utilisateur));
        model.addAttribute("statutsDevis", StatutDevis.values());
        model.addAttribute("statutSelectionne", statut);
        return "devis/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(@RequestParam(required = false) Long materielId, Model model) {
        Devis devis = new Devis();
        if (materielId != null) {
            Materiel materiel = materielService.trouverParId(materielId);
            devis.setMateriel(materiel);
            devis.setClient(materiel.getClient());
        }
        devis.getLignes().add(new LigneDevis());
        preparerFormulaire(model, devis);
        return "devis/formulaire";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Devis devis = devisService.trouverParId(id);
        if (devis.getStatut() != StatutDevis.EN_ATTENTE) {
            redirectAttributes.addFlashAttribute("erreur", "Un devis déjà validé, refusé ou expiré ne peut plus être modifié.");
            return "redirect:/devis/" + id;
        }
        if (devis.getLignes().isEmpty()) {
            devis.getLignes().add(new LigneDevis());
        }
        preparerFormulaire(model, devis);
        return "devis/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute("devis") Devis devis,
                              @RequestParam(value = "designations", required = false) List<String> designations,
                              @RequestParam(value = "quantites", required = false) List<Integer> quantites,
                              @RequestParam(value = "prixUnitaires", required = false) List<BigDecimal> prixUnitaires,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            Devis sauvegarde = devisService.enregistrer(devis, designations, quantites, prixUnitaires, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Devis enregistré sous le numéro " + sauvegarde.getNumeroDevis() + ".");
            return "redirect:/devis/" + sauvegarde.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            if (devis.getLignes().isEmpty()) {
                devis.getLignes().add(new LigneDevis());
            }
            preparerFormulaire(model, devis);
            return "devis/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Devis devis = devisService.trouverParId(id);
        if (!devisService.peutAcceder(devis, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        model.addAttribute("menuActif", "devis");
        model.addAttribute("devis", devis);
        return "devis/details";
    }

    @GetMapping("/{id}/imprimer")
    public String imprimer(@PathVariable Long id, Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Devis devis = devisService.trouverParId(id);
        if (!devisService.peutAcceder(devis, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        model.addAttribute("devis", devis);
        return "devis/imprimer";
    }

    @PostMapping("/{id}/accepter")
    public String accepter(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            devisService.accepter(id, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Devis accepté. Vous pouvez maintenant démarrer la réparation.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/devis/" + id;
    }

    @PostMapping("/{id}/refuser")
    public String refuser(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            devisService.refuser(id, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Devis marqué comme refusé.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/devis/" + id;
    }

    @PostMapping("/{id}/expirer")
    public String expirer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            devisService.marquerExpire(id);
            redirectAttributes.addFlashAttribute("succes", "Devis marqué comme expiré.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/devis/" + id;
    }

    @PostMapping("/{id}/nouvelle-version")
    public String nouvelleVersion(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Devis nouveau = devisService.creerNouvelleVersion(id, utilisateur);
        redirectAttributes.addFlashAttribute("succes", "Nouvelle version créée (" + nouveau.getNumeroDevis() + "). Vous pouvez l'ajuster.");
        return "redirect:/devis/" + nouveau.getId() + "/modifier";
    }

    private void preparerFormulaire(Model model, Devis devis) {
        model.addAttribute("menuActif", "devis");
        model.addAttribute("devis", devis);
        model.addAttribute("materiels", materielService.listerTous());
    }
}
