package licence.projetlrb.Services;

import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Repositories.DevoirRepository;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.Repositories.PartieDevoirRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DevoirService {

    @Autowired
    private DevoirRepository devoirRepository;

    @Autowired
    private PartieDevoirRepository partieDevoirRepository;

    @Autowired
    private NotationRepository notationRepository;

    @Transactional
    public ResponseDTO<Devoir> enregistrer(Devoir devoir) {
        try {
            if (devoir.getType() == null || devoir.getType().trim().isEmpty()) {
                return ResponseDTO.error("Le type du devoir est requis");
            }
            if (devoir.getDateDevoir() == null) {
                return ResponseDTO.error("La date du devoir est requise");
            }
            if (devoir.getCoefficient() == null) {
                return ResponseDTO.error("Le coefficient du devoir est requis");
            }
            if (devoir.getIdClasse() == null) {
                return ResponseDTO.error("L'ID de la classe est requis");
            }
            if (devoir.getIdMatiere() == null) {
                return ResponseDTO.error("L'ID de la matière est requis");
            }

            Devoir savedDevoir= devoirRepository.save(devoir);
            return ResponseDTO.success(savedDevoir);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement du devoir: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idDevoir) {
        try {
            Devoir devoir = devoirRepository.findById(idDevoir)
                    .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

            // Supprimer les notations associées
            List<Partie_Devoir> parties = partieDevoirRepository.findByIdDevoir(idDevoir);
            for (Partie_Devoir partie : parties) {
                List<Notation> notations = notationRepository.findByIdPartie(partie.getId());
                notationRepository.deleteAll(notations);
            }
            partieDevoirRepository.deleteAll(parties);
            devoirRepository.delete(devoir);


            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression du devoir: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Devoir>> rechercherDevoirs() {
        try {
            List<Devoir> devoirs = devoirRepository.findAll();
            return ResponseDTO.success(devoirs);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des devoirs: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Devoir> rechercherParId(Integer id) {
        try {
            if (id == null) {
                return ResponseDTO.error("L'ID du devoir est requis");
            }

            return devoirRepository.findById(id)
                    .map(ResponseDTO::success)
                    .orElse(ResponseDTO.error("Aucun devoir trouvé avec l'ID: " + id));

        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche du devoir: " + e.getMessage());
        }
    }
}