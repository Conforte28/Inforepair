package bj.inforepair.service;

import bj.inforepair.entity.ParametresEntreprise;
import bj.inforepair.repository.ParametresEntrepriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParametresEntrepriseService {

    private final ParametresEntrepriseRepository repository;

    public ParametresEntrepriseService(ParametresEntrepriseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ParametresEntreprise getParametres() {
        List<ParametresEntreprise> liste = repository.findAll();
        if (!liste.isEmpty()) {
            return liste.get(0);
        }
        ParametresEntreprise defaut = new ParametresEntreprise();
        defaut.setNomEntreprise("INFOREPAIR");
        defaut.setCommune("Cotonou");
        defaut.setAdresse("Quartier Haie Vive");
        defaut.setTelephone("+229 01 00 00 00 00");
        defaut.setEmail("contact@inforepair.bj");
        return repository.save(defaut);
    }

    @Transactional
    public ParametresEntreprise enregistrer(ParametresEntreprise donnees) {
        ParametresEntreprise existant = getParametres();
        existant.setNomEntreprise(donnees.getNomEntreprise().trim());
        existant.setCommune(donnees.getCommune());
        existant.setAdresse(donnees.getAdresse());
        existant.setTelephone(donnees.getTelephone());
        existant.setEmail(donnees.getEmail());
        return repository.save(existant);
    }
}
