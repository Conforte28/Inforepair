package bj.inforepair.controller;

import bj.inforepair.entity.*;
import bj.inforepair.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plaintes")
public class PlainteController {

    private final PlainteService plainteService;
    private final ClientService clientService;
    private final MaterielService materielService;
    private final ReparationService reparationService;
    private final UtilisateurService utilisateurService;

    public PlainteController(PlainteService plainteService,
                             ClientService clientService,
                             MaterielService materielService,
                             ReparationService reparationService,
                             UtilisateurService utilisateurService) {
        this.plainteService = plainteService;
        this.clientService = clientService;
        this.materielService = materielService;
        this.reparationService = reparationService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) StatutPlainte statut,
                        Authentication authentication,
                        Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        model.addAttribute("menuActif", "plaintes");
        model.addAttribute("plaintes", plainteService.filtrer(q, statut, utilisateur));
        model.addAttribute("statutsPlainte", StatutPlainte.values());
        model.addAttribute("q", q);
        model.addAttribute("statutSelectionne", statut);
        return "plaintes/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(@RequestParam(required = false) Long clientId,
                          @RequestParam(required = false) Long materielId,
                          @RequestParam(required = false) Long reparationId,
                          Model model) {
        Plainte plainte = new Plainte();
        if (reparationId != null) {
            Reparation r = reparationService.trouverParId(reparationId);
            plainte.setReparation(r);
            plainte.setMateriel(r.getMateriel());
            plainte.setClient(r.getClient());
        } else if (materielId != null) {
            Materiel m = materielService.trouverParId(materielId);
            plainte.setMateriel(m);
            plainte.setClient(m.getClient());
        } else if (clientId != null) {
            plainte.setClient(clientService.trouverParId(clientId));
        }
        preparerFormulaire(model, plainte);
        return "plaintes/formulaire";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Plainte plainte = plainteService.trouverParId(id);
        if (plainte.getStatut() == StatutPlainte.CLOTUREE) {
            redirectAttributes.addFlashAttribute("erreur", "Une plainte clôturée ne peut plus être modifiée.");
            return "redirect:/plaintes/" + id;
        }
        preparerFormulaire(model, plainte);
        return "plaintes/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("plainte") Plainte plainte,
                              BindingResult result,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(model, plainte);
            return "plaintes/formulaire";
        }
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            Plainte sauvegarde = plainteService.enregistrer(plainte, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Plainte enregistrée (" + sauvegarde.getNumeroPlainte() + ").");
            return "redirect:/plaintes/" + sauvegarde.getId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/plaintes/" + plainte.getId();
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Plainte plainte = plainteService.trouverParId(id);
        if (!plainteService.peutAcceder(plainte, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        model.addAttribute("menuActif", "plaintes");
        model.addAttribute("plainte", plainte);
        model.addAttribute("historiques", plainteService.listerHistorique(id));
        model.addAttribute("utilisateurs", utilisateurService.listerActifs());
        model.addAttribute("statutsPlainte", StatutPlainte.values());
        return "plaintes/details";
    }

    @PostMapping("/{id}/traiter")
    public String traiter(@PathVariable Long id,
                          @RequestParam(required = false) StatutPlainte nouveauStatut,
                          @RequestParam(required = false) Long responsableId,
                          @RequestParam(required = false) String actionRealisee,
                          @RequestParam(required = false) String solutionApportee,
                          @RequestParam(required = false) String observation,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            plainteService.traiter(id, nouveauStatut, responsableId, actionRealisee, solutionApportee, observation, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Suivi de la plainte enregistré.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/plaintes/" + id;
    }

    private void preparerFormulaire(Model model, Plainte plainte) {
        model.addAttribute("menuActif", "plaintes");
        model.addAttribute("plainte", plainte);
        model.addAttribute("clients", clientService.listerActifs());
        model.addAttribute("materiels", materielService.listerTous());
        model.addAttribute("reparations", reparationService.listerToutes());
        model.addAttribute("utilisateurs", utilisateurService.listerActifs());
        model.addAttribute("typesPlainte", TypePlainte.values());
    }
}
