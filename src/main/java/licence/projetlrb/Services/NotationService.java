package licence.projetlrb.Services;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Repositories.NotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotationService {

    private final NotationRepository notationRepository;

    @Autowired
    public NotationService(NotationRepository notationRepository) {
        this.notationRepository = notationRepository;
    }

    @Transactional
    public ResponseDTO<Notation> enregistrer(Map<String, Object> notationData) {
        try {
            String idEtudiantString = (String) notationData.get("idEtudiant");
            String idDevoirString = (String) notationData.get("idDevoir");
            String noteString = (String) notationData.get("note");

            if (idEtudiantString == null || idDevoirString == null || noteString == null) {
                return ResponseDTO.error("L'ID de l'étudiant, l'ID du devoir et la note sont requis");
            }

            Integer idEtudiant = Integer.parseInt(idEtudiantString);
            Integer idDevoir = Integer.parseInt(idDevoirString);
            BigDecimal note = new BigDecimal(noteString);



            // Validation des données
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            if (note == null) {
                return ResponseDTO.error("La note est requise");
            }
            if (idDevoir == null) {
                return ResponseDTO.error("L'ID du devoir est requis");
            }
            if (note.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseDTO.error("La note ne peut pas être négative");
            }

            // Recherche si une notation existe déjà pour cet étudiant et ce devoir
            Optional<Notation> existingNotation = notationRepository.findByIdEtudiantAndIdDevoir(idEtudiant, idDevoir);

            Notation savedNotation;
            if (existingNotation.isPresent()) {
                // Mise à jour de la notation existante
                Notation notationToUpdate = existingNotation.get();
                notationToUpdate.setNote(note);
                savedNotation = notationRepository.save(notationToUpdate);
            } else {
                // Création d'une nouvelle notation
                Notation newNotation = new Notation();
                newNotation.setIdEtudiant(idEtudiant);
                newNotation.setNote(note);
                newNotation.setIdDevoir(idDevoir);
                savedNotation = notationRepository.save(newNotation);
            }

            return ResponseDTO.success(savedNotation);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la notation: " + e.getMessage());
        }
    }
    @Transactional
    public ResponseDTO<Void> supprimer(Integer idNotation) {
        try {
            if (!notationRepository.existsById(Long.valueOf(idNotation))) {
                return ResponseDTO.error("Notation non trouvée avec l'ID: " + idNotation);
            }
            notationRepository.deleteById(Long.valueOf(idNotation));
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la notation: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public ResponseDTO<List<Notation>> rechercherNotationsParDevoir(Integer idDevoir) {
        try {
            List<Notation> notations = notationRepository.findByIdDevoir(idDevoir);
            return ResponseDTO.success(notations);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des notations: " + e.getMessage());
        }
    }
}