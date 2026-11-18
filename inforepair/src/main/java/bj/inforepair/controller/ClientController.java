package bj.inforepair.controller;

import bj.inforepair.entity.Client;
import bj.inforepair.repository.MaterielRepository;
import bj.inforepair.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final MaterielRepository materielRepository;
    private final DevisService devisService;
    private final ReparationService reparationService;
    private final PlainteService plainteService;
    private final PaiementService paiementService;

    public ClientController(ClientService clientService,
                            MaterielRepository materielRepository,
                            DevisService devisService,
                            ReparationService reparationService,
                            PlainteService plainteService,
                            PaiementService paiementService) {
        this.clientService = clientService;
        this.materielRepository = materielRepository;
        this.devisService = devisService;
        this.reparationService = reparationService;
        this.plainteService = plainteService;
        this.paiementService = paiementService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("menuActif", "clients");
        model.addAttribute("clients", clientService.lister(q));
        model.addAttribute("q", q);
        return "clients/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("menuActif", "clients");
        model.addAttribute("client", new Client());
        return "clients/formulaire";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("menuActif", "clients");
        model.addAttribute("client", clientService.trouverParId(id));
        return "clients/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("client") Client client,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("menuActif", "clients");
            return "clients/formulaire";
        }
        Client sauvegarde = clientService.enregistrer(client);
        redirectAttributes.addFlashAttribute("succes", "Fiche client enregistrée (" + sauvegarde.getCodeClient() + ").");
        return "redirect:/clients/" + sauvegarde.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Client client = clientService.trouverParId(id);
        model.addAttribute("menuActif", "clients");
        model.addAttribute("client", client);
        model.addAttribute("materiels", materielRepository.findByClientIdOrderByIdDesc(id));
        model.addAttribute("devisListe", devisService.listerParClient(id));
        model.addAttribute("reparations", reparationService.listerParClient(id));
        model.addAttribute("plaintes", plainteService.listerParClient(id));
        model.addAttribute("paiements", paiementService.listerParClient(id));
        model.addAttribute("peutSupprimer", clientService.peutSupprimer(id));
        return "clients/details";
    }

    @PostMapping("/{id}/basculer-actif")
    public String basculerActif(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clientService.basculerActif(id);
        redirectAttributes.addFlashAttribute("succes", "Statut du client mis à jour.");
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Client supprimé.");
            return "redirect:/clients";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/clients/" + id;
        }
    }
}
