package licence.projetlrb.Services;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Repositories.ClasseRepository;
import licence.projetlrb.Repositories.EtudiantRepository;
import licence.projetlrb.DTO.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClasseService {
    private static final Logger logger = LoggerFactory.getLogger(ClasseService.class);

    private final ClasseRepository classeRepository;
    private final EtudiantRepository etudiantRepository;

    @Autowired
    public ClasseService(ClasseRepository classeRepository, EtudiantRepository etudiantRepository) {
        this.classeRepository = classeRepository;
        this.etudiantRepository = etudiantRepository;
    }

    @Transactional
    public ResponseDTO<Classe> enregistrer(Classe classe) {
        try {
            validateClasse(classe);

            // Nettoyage de la dénomination
            classe.setDenomination(classe.getDenomination().trim());

            // Vérification des doublons
            Optional<Classe> existingClasse = classeRepository
                    .findByDenominationIgnoreCase(classe.getDenomination());
            if (existingClasse.isPresent() &&
                    (classe.getId() == null || !classe.getId().equals(existingClasse.get().getId()))) {
                return ResponseDTO.error("Une classe avec cette dénomination existe déjà");
            }

            Classe savedClasse = classeRepository.save(classe);
            logger.info("Classe enregistrée avec succès: {}", savedClasse.getDenomination());
            return ResponseDTO.success(savedClasse);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de l'enregistrement de la classe", e);
            return ResponseDTO.error(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'enregistrement de la classe", e);
            return ResponseDTO.error("Une erreur est survenue lors de l'enregistrement de la classe");
        }
    }

    // Ajout d'une méthode pour récupérer le nombre d'étudiants par classe
    @Transactional(readOnly = true)
    public ResponseDTO<Integer> getNombreEtudiantsParClasse(Integer idClasse) {
        try {
            int nombreEtudiants = etudiantRepository.countByIdClasse(idClasse);
            return ResponseDTO.success(nombreEtudiants);
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des étudiants pour la classe {}", idClasse, e);
            return ResponseDTO.error("Erreur lors du comptage des étudiants");
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<Classe>> rechercherClasses() {
        try {
            List<Classe> classes = classeRepository.findAll();
            logger.debug("Nombre de classes trouvées: {}", classes.size());
            return ResponseDTO.success(classes);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche des classes", e);
            return ResponseDTO.error("Erreur lors de la récupération des classes");
        }
    }

    // Méthode pour rechercher les classes avec le nombre d'étuional(readOnly = true)
    public ResponseDTO<List<Map<String, Object>>> rechercherClassesAvecNombreEtudiants() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            List<Classe> classes = classeRepository.findAll();

            for (Classe classe : classes) {
                Map<String, Object> classeInfo = new HashMap<>();
                classeInfo.put("classe", classe);
                classeInfo.put("nombreEtudiants",
                        etudiantRepository.countByIdClasse(classe.getId()));
                result.add(classeInfo);
            }

            return ResponseDTO.success(result);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche des classes avec nombre d'étudiants", e);
            return ResponseDTO.error("Erreurations des classes");
        }
    }

    // Amélioration de la validation
    private void validateClasse(Classe classe) {
        if (classe == null) {
            throw new IllegalArgumentException("La classe ne peut pas être null");
        }
        if (classe.getDenomination() == null || classe.getDenomination().trim().isEmpty()) {
            throw new IllegalArgumentException("La dénomination de la classe est requise");
        }
        if (classe.getDenomination().length() > 50) {  // Ajustez selon vos besoins
            throw new IllegalArgumentException("La dénomination ne peut pas dépasser 50 caractères");
        }
    }
    @Transactional
    public ResponseDTO<Void> supprimer(Integer idClasse) {
        try {
            if (idClasse == null) {
                return ResponseDTO.error("L'ID de la classe est requis");
            }

            Optional<Classe> classeOpt = classeRepository.findById(idClasse);
            if (classeOpt.isEmpty()) {
                return ResponseDTO.error("Classe non trouvée");
            }

            Classe classe = classeOpt.get();

            // Libérer les étudiants associés
            List<Etudiant> etudiants = etudiantRepository.findByIdClasse(idClasse);
            for (Etudiant etudiant : etudiants) {
                etudiant.setIdClasse(null);
            }
            etudiantRepository.saveAll(etudiants);

            // Supprimer la classe
            classeRepository.delete(classe);
            logger.info("Classe supprimée avec succès: {}", classe.getDenomination());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la classe avec l'ID: {}", idClasse, e);
            return ResponseDTO.error("Erreur lors de la suppression de la classe");
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Classe> rechercherParId(Integer id) {
        try {
            if (id == null) {
                return ResponseDTO.error("L'ID de la classe est requis");
            }

            Optional<Classe> classeOpt = classeRepository.findById(id);
            if (classeOpt.isEmpty()) {
                logger.debug("Aucune classe trouvée avec l'ID: {}", id);
                return ResponseDTO.error("Classe non trouvée");
            }

            Classe classe = classeOpt.get();
            logger.debug("Classe trouvée: {}", classe.getDenomination());
            return ResponseDTO.success(classe);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la classe avec l'ID: {}", id, e);
            return ResponseDTO.error("Erreur lors de la recherche de la classe");
        }
    }

}