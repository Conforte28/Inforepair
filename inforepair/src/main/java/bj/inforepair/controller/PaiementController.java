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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final ReparationService reparationService;
    private final UtilisateurService utilisateurService;

    public PaiementController(PaiementService paiementService,
                              ReparationService reparationService,
                              UtilisateurService utilisateurService) {
        this.paiementService = paiementService;
        this.reparationService = reparationService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String liste(Model model) {
        List<Paiement> paiements = paiementService.listerTous();
        Map<Long, BigDecimal> restes = new LinkedHashMap<>();
        for (Paiement p : paiements) {
            Long repId = p.getReparation().getId();
            restes.computeIfAbsent(repId, k -> paiementService.getResteAPayer(p.getReparation()));
        }
        model.addAttribute("menuActif", "paiements");
        model.addAttribute("paiements", paiements);
        model.addAttribute("restesParReparation", restes);
        return "paiements/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(@RequestParam(required = false) Long reparationId, Model model) {
        List<Reparation> reparations = reparationService.listerToutes();
        Map<Long, BigDecimal> restes = new LinkedHashMap<>();
        Map<Long, BigDecimal> payes = new LinkedHashMap<>();
        for (Reparation r : reparations) {
            restes.put(r.getId(), paiementService.getResteAPayer(r));
            payes.put(r.getId(), paiementService.getTotalPaye(r.getId()));
        }
        model.addAttribute("menuActif", "paiements");
        model.addAttribute("reparations", reparations);
        model.addAttribute("restesParReparation", restes);
        model.addAttribute("payesParReparation", payes);
        model.addAttribute("modesPaiement", ModePaiement.values());
        model.addAttribute("reparationSelectionneeId", reparationId);
        model.addAttribute("dateDefaut", LocalDate.now());
        if (reparationId != null) {
            Reparation r = reparationService.trouverParId(reparationId);
            model.addAttribute("reparationSelectionnee", r);
            model.addAttribute("resteSelectionne", restes.get(reparationId));
        }
        return "paiements/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long reparationId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
                              @RequestParam BigDecimal montant,
                              @RequestParam ModePaiement modePaiement,
                              @RequestParam(required = false) String reference,
                              @RequestParam(required = false) String observation,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            Paiement paiement = paiementService.enregistrer(reparationId, datePaiement, montant, modePaiement, reference, observation, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Paiement enregistré (" + paiement.getNumeroPaiement() + ").");
            return "redirect:/reparations/" + reparationId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/paiements/nouveau?reparationId=" + reparationId;
        }
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id,
                          @RequestParam String motif,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
            paiementService.annuler(id, motif, utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Paiement annulé avec conservation dans l'historique.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/paiements";
    }
}
