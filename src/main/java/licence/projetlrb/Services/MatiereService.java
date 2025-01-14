package licence.projetlrb.Services;

import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Matiere;
import licence.projetlrb.Repositories.DevoirRepository;
import licence.projetlrb.Repositories.MatiereRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;
    @Autowired
    private DevoirRepository devoirRepository;

    @Transactional
    public ResponseDTO<Matiere> enregistrer(Matiere matiere) {
        try {
            if (matiere.getDenomination() == null || matiere.getDenomination().trim().isEmpty()) {
                return ResponseDTO.error("La dénomination de la matière est requise");
            }
            Matiere savedMatiere = matiereRepository.save(matiere);
            return ResponseDTO.success(savedMatiere);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la matière: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idMatiere) {
        try {
            Matiere matiere = matiereRepository.findById(idMatiere)
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

            //verifier si la matiere est utilisée dans un devoir
            List<Devoir> devoirs = devoirRepository.findAll();
            for (Devoir devoir : devoirs) {
                if (devoir.getIdMatiere().equals(idMatiere)){
                    return ResponseDTO.error("Impossible de supprimer cette matière car elle est utilisée dans un devoir");
                }
            }

            matiereRepository.delete(matiere);
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la matière: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Matiere>> rechercherMatieres() {
        try {
            List<Matiere> matieres = matiereRepository.findAll();
            return ResponseDTO.success(matieres);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des matières: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Matiere> rechercherParId(Integer id) {
        try {
            if (id == null) {
                return ResponseDTO.error("L'ID de la matière est requis");
            }

            return matiereRepository.findById(id)
                    .map(ResponseDTO::success)
                    .orElse(ResponseDTO.error("Aucune matière trouvée avec l'ID: " + id));

        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche de la matière: " + e.getMessage());
        }
    }
}