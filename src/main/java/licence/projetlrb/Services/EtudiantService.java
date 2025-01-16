package licence.projetlrb.Services;

import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Repositories.ClasseRepository;
import licence.projetlrb.Repositories.EtudiantRepository;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final NotationRepository notationRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public EtudiantService(EtudiantRepository etudiantRepository, NotationRepository notationRepository, ClasseRepository classeRepository) {
        this.etudiantRepository = etudiantRepository;
        this.notationRepository = notationRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public ResponseDTO<Etudiant> enregistrer(Etudiant etudiant) {
        try {
            if (etudiant == null) {
                return ResponseDTO.error("L'étudiant ne peut pas être null");
            }
            if (etudiant.getNom() == null || etudiant.getNom().trim().isEmpty()) {
                return ResponseDTO.error("Le nom de l'étudiant est requis");
            }
            if (etudiant.getPrenom() == null || etudiant.getPrenom().trim().isEmpty()) {
                return ResponseDTO.error("Le prénom de l'étudiant est requis");
            }

            // Nettoyage des champs
            etudiant.setNom(etudiant.getNom().trim());
            etudiant.setPrenom(etudiant.getPrenom().trim());

            // Gestion de la photo
            if (etudiant.getPhoto() != null) {
                String photoTrimmed = etudiant.getPhoto().trim();
                // Si la photo est vide après le trim, on met null
                etudiant.setPhoto(photoTrimmed.isEmpty() ? null : photoTrimmed);
            }

            Etudiant savedEtudiant = etudiantRepository.save(etudiant);
            return ResponseDTO.success("Étudiant enregistré avec succès", savedEtudiant);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de l'étudiant: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<String, Object>> rechercherEtudiantsAvecClasses() {
        try {
            List<Etudiant> etudiants = etudiantRepository.findAll();
            Map<Integer, String> nomsClasses = new HashMap<>();

            for (Etudiant etudiant : etudiants) {
                if (etudiant.getIdClasse() != null) {
                    String nomClasse = getNomClasse(etudiant.getIdClasse());
                    nomsClasses.put(etudiant.getIdClasse(), nomClasse);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("etudiants", etudiants);
            result.put("nomsClasses", nomsClasses);

            return ResponseDTO.success("Liste des étudiants récupérée avec succès", result);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String getNomClasse(Integer idClasse) {
        if (idClasse == null) return "Non assigné";
        return classeRepository.findById(idClasse)
                .map(Classe::getDenomination)
                .orElse("Non assigné");
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idEtudiant) {
        try {
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant ne peut pas être null");
            }

            Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
            if (etudiantOpt.isEmpty()) {
                return ResponseDTO.error("Étudiant non trouvé avec l'ID: " + idEtudiant);
            }

            notationRepository.deleteByIdEtudiant(idEtudiant);
            etudiantRepository.delete(etudiantOpt.get());
            return ResponseDTO.success("Étudiant supprimé avec succès", null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<Etudiant>> rechercherEtudiants() {
        try {
            List<Etudiant> etudiants = etudiantRepository.findAll();
            return ResponseDTO.success("Liste des étudiants récupérée avec succès", etudiants);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<Etudiant>> rechercherEtudiantsDisponibles() {
        try {
            List<Etudiant> etudiants = etudiantRepository.findByIdClasseIsNull();
            return ResponseDTO.success("Liste des étudiants disponibles récupérée avec succès", etudiants);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants disponibles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Etudiant> rechercherParId(Integer id) {
        try {
            if (id == null) {
                return ResponseDTO.error("L'ID de l'étudiant ne peut pas être null");
            }

            Optional<Etudiant> etudiant = etudiantRepository.findById(id);
            if (etudiant.isEmpty()) {
                return ResponseDTO.error("Aucun étudiant trouvé avec l'ID: " + id);
            }

            return ResponseDTO.success("Étudiant trouvé avec succès", etudiant.get());
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche de l'étudiant: " + e.getMessage());
        }
    }
    public ResponseDTO<List<Etudiant>> rechercherEtudiantsParClasse(Integer idClasse) {
        try {
            List<Etudiant> etudiants = etudiantRepository.findByIdClasse(idClasse);
            return ResponseDTO.success(etudiants);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public ResponseDTO<List<Etudiant>> rechercherParNomOuPrenom(String terme) {
        try {
            if (terme == null || terme.trim().isEmpty()) {
                return ResponseDTO.error("Le terme de recherche ne peut pas être vide");
            }

            String termeTrimmed = terme.trim().toLowerCase();
            List<Etudiant> etudiants = etudiantRepository.findAll().stream()
                    .filter(e -> e.getNom().toLowerCase().contains(termeTrimmed) ||
                            e.getPrenom().toLowerCase().contains(termeTrimmed))
                    .toList();

            return ResponseDTO.success(
                    "Recherche effectuée avec succès",
                    etudiants
            );
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants: " + e.getMessage());
        }
    }
}