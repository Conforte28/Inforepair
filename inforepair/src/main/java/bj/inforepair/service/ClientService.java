package bj.inforepair.service;

import bj.inforepair.entity.Client;
import bj.inforepair.repository.ClientRepository;
import bj.inforepair.repository.DevisRepository;
import bj.inforepair.repository.MaterielRepository;
import bj.inforepair.repository.PlainteRepository;
import bj.inforepair.repository.ReparationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final MaterielRepository materielRepository;
    private final DevisRepository devisRepository;
    private final ReparationRepository reparationRepository;
    private final PlainteRepository plainteRepository;

    public ClientService(ClientRepository clientRepository,
                         MaterielRepository materielRepository,
                         DevisRepository devisRepository,
                         ReparationRepository reparationRepository,
                         PlainteRepository plainteRepository) {
        this.clientRepository = clientRepository;
        this.materielRepository = materielRepository;
        this.devisRepository = devisRepository;
        this.reparationRepository = reparationRepository;
        this.plainteRepository = plainteRepository;
    }

    public List<Client> lister(String terme) {
        if (terme != null && !terme.trim().isEmpty()) {
            return clientRepository.rechercher(terme.trim());
        }
        return clientRepository.findAllByOrderByIdDesc();
    }

    public List<Client> listerActifs() {
        return clientRepository.findByActifTrueOrderByNomCompletAsc();
    }

    public Client trouverParId(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable."));
    }

    @Transactional
    public Client enregistrer(Client client) {
        if (client.getId() == null) {
            if (client.getDateEnregistrement() == null) {
                client.setDateEnregistrement(LocalDate.now());
            }
            client.setCodeClient("TMP-" + System.nanoTime());
            Client sauvegarde = clientRepository.save(client);
            sauvegarde.setCodeClient(String.format("CLI-%d-%04d", sauvegarde.getDateEnregistrement().getYear(), sauvegarde.getId()));
            return clientRepository.save(sauvegarde);
        }
        Client existant = trouverParId(client.getId());
        existant.setNomComplet(client.getNomComplet().trim());
        existant.setTelephone(client.getTelephone().trim());
        existant.setEmail(client.getEmail());
        existant.setAdresseCommune(client.getAdresseCommune());
        existant.setObservation(client.getObservation());
        existant.setActif(client.isActif());
        return clientRepository.save(existant);
    }

    public boolean peutSupprimer(Long clientId) {
        return !materielRepository.existsByClientId(clientId)
                && !devisRepository.existsByClientId(clientId)
                && !reparationRepository.existsByClientId(clientId)
                && !plainteRepository.existsByClientId(clientId);
    }

    @Transactional
    public void basculerActif(Long clientId) {
        Client client = trouverParId(clientId);
        client.setActif(!client.isActif());
        clientRepository.save(client);
    }

    @Transactional
    public void supprimer(Long clientId) {
        if (!peutSupprimer(clientId)) {
            throw new IllegalStateException("Ce client possède des opérations liées et ne peut pas être supprimé. Désactivez sa fiche si nécessaire.");
        }
        clientRepository.deleteById(clientId);
    }
}
