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

import java.util.List;

@Controller
@RequestMapping("/materiels")
public class MaterielController {

    private static final List<String> PANNES_COURANTES = List.of(
            "Problème d'alimentation",
            "Écran cassé ou défectueux",
            "Problème de batterie",
            "Problème de clavier",
            "Problème de disque dur ou SSD",
            "Problème de mémoire RAM",
            "Problème de système d'exploitation",
            "Virus ou logiciel malveillant",
            "Problème de connexion réseau",
            "Problème de carte mère",
            "Autre panne"
    );

    private final MaterielService materielService;
    private final ClientService clientService;
    private final DevisService devisService;
    private final ReparationService reparationService;
    private final PlainteService plainteService;
    private final UtilisateurService utilisateurService;

    public MaterielController(MaterielService materielService,
                              ClientService clientService,
                              DevisService devisService,
                              ReparationService reparationService,
                              PlainteService plainteService,
                              UtilisateurService utilisateurService) {
        this.materielService = materielService;
        this.clientService = clientService;
        this.devisService = devisService;
        this.reparationService = reparationService;
        this.plainteService = plainteService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) TypeMateriel type,
                        @RequestParam(required = false) EtatMateriel etat,
                        Authentication authentication,
                        Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        model.addAttribute("menuActif", "materiels");
        model.addAttribute("materiels", materielService.filtrer(q, type, etat, utilisateur));
        model.addAttribute("typesMateriel", TypeMateriel.values());
        model.addAttribute("etatsMateriel", EtatMateriel.values());
        model.addAttribute("q", q);
        model.addAttribute("typeSelectionne", type);
        model.addAttribute("etatSelectionne", etat);
        return "materiels/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(@RequestParam(required = false) Long clientId, Model model) {
        Materiel materiel = new Materiel();
        if (clientId != null) {
            materiel.setClient(clientService.trouverParId(clientId));
        }
        preparerFormulaire(model, materiel);
        return "materiels/formulaire";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        preparerFormulaire(model, materielService.trouverParId(id));
        return "materiels/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("materiel") Materiel materiel,
                              BindingResult result,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(model, materiel);
            return "materiels/formulaire";
        }
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Materiel sauvegarde = materielService.enregistrer(materiel, utilisateur);
        redirectAttributes.addFlashAttribute("succes", "Matériel enregistré sous le dossier " + sauvegarde.getNumeroDossier() + ".");
        return "redirect:/materiels/" + sauvegarde.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        Materiel materiel = materielService.trouverParId(id);
        if (!materielService.peutAcceder(materiel, utilisateur)) {
            return "redirect:/acces-refuse";
        }
        model.addAttribute("menuActif", "materiels");
        model.addAttribute("materiel", materiel);
        model.addAttribute("historiques", materielService.listerHistorique(id));
        model.addAttribute("devisListe", devisService.listerParMateriel(id));
        model.addAttribute("reparations", reparationService.listerParMateriel(id));
        model.addAttribute("plaintes", plainteService.listerParMateriel(id));
        model.addAttribute("etatsMateriel", EtatMateriel.values());
        model.addAttribute("peutSupprimer", materielService.peutSupprimer(id));
        return "materiels/details";
    }

    @PostMapping("/{id}/etat")
    public String changerEtat(@PathVariable Long id,
                              @RequestParam EtatMateriel nouvelEtat,
                              @RequestParam(required = false) String observation,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(authentication.getName()).orElse(null);
        materielService.changerEtat(id, nouvelEtat, utilisateur, observation);
        redirectAttributes.addFlashAttribute("succes", "État du matériel mis à jour.");
        return "redirect:/materiels/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            materielService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Matériel supprimé.");
            return "redirect:/materiels";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/materiels/" + id;
        }
    }

    private void preparerFormulaire(Model model, Materiel materiel) {
        model.addAttribute("menuActif", "materiels");
        model.addAttribute("materiel", materiel);
        model.addAttribute("clients", clientService.listerActifs());
        model.addAttribute("typesMateriel", TypeMateriel.values());
        model.addAttribute("etatsMateriel", EtatMateriel.values());
        model.addAttribute("pannesCourantes", PANNES_COURANTES);
    }
}
