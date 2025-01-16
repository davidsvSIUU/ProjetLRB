package licence.projetlrb.Services;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Repositories.DevoirRepository;
import licence.projetlrb.Repositories.EtudiantRepository;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.Repositories.PartieDevoirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DevoirService {

    private final DevoirRepository devoirRepository;
    private final PartieDevoirRepository partieDevoirRepository;
    private final NotationRepository notationRepository;
    private final EtudiantRepository etudiantRepository;

    @Autowired
    public DevoirService(DevoirRepository devoirRepository,
                         PartieDevoirRepository partieDevoirRepository,
                         NotationRepository notationRepository,
                         EtudiantRepository etudiantRepository) {
        this.devoirRepository = devoirRepository;
        this.partieDevoirRepository = partieDevoirRepository;
        this.notationRepository = notationRepository;
        this.etudiantRepository = etudiantRepository;
    }


    @Transactional
    public ResponseDTO<Devoir> enregistrer(Devoir devoir, List<String> pointsParPartie) {
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

            Devoir savedDevoir = devoirRepository.save(devoir);
            // Enregistrement des parties de devoir
            if (pointsParPartie != null && !pointsParPartie.isEmpty()) {
                for (String points : pointsParPartie) {
                    Partie_Devoir partie = new Partie_Devoir();
                    partie.setIdDevoir(savedDevoir.getId());
                    partie.setPoints(new BigDecimal(points));
                    partieDevoirRepository.save(partie);
                }
            }
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

            // Supprimer les parties de devoirs et leurs notations associées
            List<Partie_Devoir> parties = partieDevoirRepository.findByIdDevoir(idDevoir);
            for (Partie_Devoir partie : parties) {
                List<Etudiant> etudiants = etudiantRepository.findAll();
                for (Etudiant etudiant : etudiants){
                    List<Notation> notations = notationRepository.findByIdEtudiant(etudiant.getId());
                    notationRepository.deleteAll(notations);
                }

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

    @Transactional(readOnly = true)
    public ResponseDTO<List<Partie_Devoir>> rechercherPartiesParId(Integer id) {
        try {
            List<Partie_Devoir> parties = partieDevoirRepository.findByIdDevoir(id);
            return ResponseDTO.success(parties);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des parties du devoir: " + e.getMessage());
        }
    }
}