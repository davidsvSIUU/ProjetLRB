package licence.projetlrb.Services;

import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Repositories.EtudiantRepository;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private NotationRepository notationRepository;

    @Transactional
    public ResponseDTO<Etudiant> enregistrer(Etudiant etudiant) {
        try {
            if (etudiant.getNom() == null || etudiant.getNom().trim().isEmpty()) {
                return ResponseDTO.error("Le nom de l'étudiant est requis");
            }
            if (etudiant.getPrenom() == null || etudiant.getPrenom().trim().isEmpty()) {
                return ResponseDTO.error("Le prénom de l'étudiant est requis");
            }

            Etudiant savedEtudiant = etudiantRepository.save(etudiant);
            return ResponseDTO.success(savedEtudiant);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de l'étudiant: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idEtudiant) {
        try {
            // Vérifier si l'étudiant existe
            Etudiant etudiant = etudiantRepository.findById(idEtudiant)
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

            // Supprimer toutes les notations de l'étudiant
            notationRepository.deleteByIdEtudiant(idEtudiant);

            // Supprimer l'étudiant
            etudiantRepository.delete(etudiant);
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Etudiant>> rechercherEtudiants() {
        try {
            List<Etudiant> etudiants = etudiantRepository.findAll();
            return ResponseDTO.success(etudiants);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Etudiant>> rechercherEtudiantsDisponibles() {
        try {
            List<Etudiant> etudiants = etudiantRepository.findDisponibles();
            return ResponseDTO.success(etudiants);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants disponibles: " + e.getMessage());
        }
    }
}