package bj.inforepair.controller;

import bj.inforepair.service.TableauDeBordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TableauDeBordController {

    private final TableauDeBordService tableauDeBordService;

    public TableauDeBordController(TableauDeBordService tableauDeBordService) {
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("menuActif", "tableau-de-bord");
        model.addAttribute("totalMateriels", tableauDeBordService.getTotalMateriels());
        model.addAttribute("materielsAttenteDiagnostic", tableauDeBordService.getMaterielsEnAttenteDiagnostic());
        model.addAttribute("devisEnAttente", tableauDeBordService.getDevisEnAttente());
        model.addAttribute("reparationsEnCours", tableauDeBordService.getReparationsEnCours());
        model.addAttribute("reparationsTerminees", tableauDeBordService.getReparationsTerminees());
        model.addAttribute("materielsRemis", tableauDeBordService.getMaterielsRemisAuxClients());
        model.addAttribute("plaintesNonResolues", tableauDeBordService.getPlaintesNonResolues());
        model.addAttribute("montantDevisAcceptesMois", tableauDeBordService.getMontantDevisAcceptesMoisCourant());
        model.addAttribute("montantPaiementsMois", tableauDeBordService.getMontantPaiementsMoisCourant());
        model.addAttribute("derniersMateriels", tableauDeBordService.getDerniersMateriels());
        model.addAttribute("dernieresReparations", tableauDeBordService.getDernieresReparations());
        model.addAttribute("dernieresPlaintes", tableauDeBordService.getDernieresPlaintes());
        model.addAttribute("dernieresOperations", tableauDeBordService.getDernieresOperations());
        return "tableau-de-bord";
    }
}
