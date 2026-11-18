package bj.inforepair.controller;

import bj.inforepair.entity.*;
import bj.inforepair.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/reparations")
public class ReparationController {

    private final ReparationService reparationService;
    private final DevisService devisService;
    private final UtilisateurService utilisateurService;
    private final PaiementService paiementService;

    public ReparationController(ReparationService reparationService,
                                DevisService devisService,
                                UtilisateurService utilisateurService,
                                PaiementService paiementService) {
        this.reparationService = reparationService;
        this.devisService = devisService;
        this.utilisateurService = utilisateurService;
        this.paiementService = paiementService;
    }

    @GetMapping
    public String liste(Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        model.addAttribute("menuActif", "reparations");
        model.addAttribute("reparations", reparationService.lister(utilisateur));
        return "reparations/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(@RequestParam(required = false) Long devisId, Model model) {
        model.addAttribute("menuActif", "reparations");
        model.addAttribute("devisAcceptes", devisService.listerDevisAcceptes());
        model.addAttribute("techniciens", utilisateurService.listerTechniciensActifs());
        model.addAttribute("devisSelectionneId", devisId);
        model.addAttribute("dateDebutDefaut", LocalDate.now());
        return "reparations/formulaire";
    }

    @PostMapping("/demarrer")
    public String demarrer(@RequestParam Long devisId,
                           @RequestParam(required = false) Long technicienId,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                           @RequestParam(required = false) String observation,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            Reparation reparation = reparationService.demarrerDepuisDevis(devisId, technicienId, dateDebut, observation, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Réparation démarrée : " + reparation.getNumeroReparation() + ".");
            return "redirect:/reparations/" + reparation.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/reparations/nouveau" + (devisId != null ? "?devisId=" + devisId : "");
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Reparation reparation = reparationService.trouverParId(id);
        if (!reparationService.peutAcceder(reparation, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        model.addAttribute("menuActif", "reparations");
        model.addAttribute("reparation", reparation);
        model.addAttribute("interventions", reparationService.listerInterventions(id));
        model.addAttribute("techniciens", utilisateurService.listerTechniciensActifs());
        model.addAttribute("statutsReparation", StatutReparation.values());
        model.addAttribute("paiements", paiementService.listerParReparation(id));
        model.addAttribute("montantDu", paiementService.getMontantDu(reparation));
        model.addAttribute("totalPaye", paiementService.getTotalPaye(id));
        model.addAttribute("resteAPayer", paiementService.getResteAPayer(reparation));
        return "reparations/details";
    }

    @PostMapping("/{id}/mettre-a-jour")
    public String mettreAJour(@PathVariable Long id,
                              @RequestParam(required = false) Long technicienId,
                              @RequestParam(required = false) StatutReparation statut,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                              @RequestParam(required = false) String descriptionTravail,
                              @RequestParam(required = false) String piecesUtilisees,
                              @RequestParam(required = false) BigDecimal coutPieces,
                              @RequestParam(required = false) BigDecimal coutMainOeuvre,
                              @RequestParam(required = false) String observation,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Reparation reparation = reparationService.trouverParId(id);
        if (!reparationService.peutAcceder(reparation, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        try {
            reparationService.mettreAJour(id, technicienId, statut, dateFin, descriptionTravail, piecesUtilisees,
                    coutPieces, coutMainOeuvre, observation, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Réparation mise à jour.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/reparations/" + id;
    }

    @PostMapping("/{id}/interventions")
    public String ajouterIntervention(@PathVariable Long id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateIntervention,
                                      @RequestParam String descriptionTravail,
                                      @RequestParam(required = false) String piecesUtilisees,
                                      @RequestParam(required = false) String observation,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Reparation reparation = reparationService.trouverParId(id);
        if (!reparationService.peutAcceder(reparation, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        try {
            reparationService.ajouterIntervention(id, dateIntervention, descriptionTravail, piecesUtilisees, observation, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Intervention enregistrée.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/reparations/" + id;
    }

    @PostMapping("/{id}/remettre")
    public String remettreAuClient(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        try {
            reparationService.remettreAuClient(id, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Matériel marqué comme remis au client.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/reparations/" + id;
    }
}
