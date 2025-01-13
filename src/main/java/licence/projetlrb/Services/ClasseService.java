package licence.projetlrb.Services;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Repositories.ClasseRepository;
import licence.projetlrb.Repositories.EtudiantRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClasseService {

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Transactional
    public ResponseDTO<Classe> enregistrer(Classe classe) {
        try {
            if (classe.getDenomination() == null || classe.getDenomination().trim().isEmpty()) {
                return ResponseDTO.error("La dénomination de la classe est requise");
            }

            Classe savedClasse = classeRepository.save(classe);
            return ResponseDTO.success(savedClasse);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la classe: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idClasse) {
        try {
            Classe classe = classeRepository.findById(idClasse)
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

            // Libérer les étudiants
            List<Etudiant> etudiants = etudiantRepository.findByIdClasse(idClasse);
            etudiants.forEach(etudiant -> etudiant.setIdClasse(null));
            etudiantRepository.saveAll(etudiants);

            classeRepository.delete(classe);
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la classe: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Classe>> rechercherClasses() {
        try {
            List<Classe> classes = classeRepository.findAll();
            return ResponseDTO.success(classes);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des classes: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Classe> rechercherParId(Integer id) {
        try {
            if (id == null) {
                return ResponseDTO.error("L'ID de la classe est requis");
            }

            return classeRepository.findById(id)
                    .map(ResponseDTO::success)
                    .orElse(ResponseDTO.error("Aucune classe trouvée avec l'ID: " + id));

        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche de la classe: " + e.getMessage());
        }
    }
}