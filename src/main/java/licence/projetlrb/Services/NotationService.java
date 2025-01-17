package licence.projetlrb.Services;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Repositories.DevoirRepository;
import licence.projetlrb.Repositories.NotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class NotationService {

    private final NotationRepository notationRepository;
    private final DevoirRepository devoirRepository;

    @Autowired
    public NotationService(NotationRepository notationRepository, DevoirRepository devoirRepository) {
        this.notationRepository = notationRepository;
        this.devoirRepository = devoirRepository;
    }

    @Transactional
    public ResponseDTO<Notation> enregistrer(Map<String, Object> notationData) {
        try {
            Integer idEtudiant = null;
            Integer idDevoir = null;
            BigDecimal note = null;
            // Récupération et validation de l'idEtudiant
            if (notationData.get("idEtudiant") instanceof Integer) {
                idEtudiant = (Integer) notationData.get("idEtudiant");
            }else if(notationData.get("idEtudiant") instanceof Number){
                idEtudiant = ((Number) notationData.get("idEtudiant")).intValue();
            }
            else {
                return ResponseDTO.error("L'ID de l'étudiant doit être un nombre");
            }


            // Récupération et validation de l'idDevoir
            if(notationData.get("idDevoir") instanceof Integer){
                idDevoir = (Integer) notationData.get("idDevoir");
            }
            else if (notationData.get("idDevoir") instanceof Number){
                idDevoir = ((Number) notationData.get("idDevoir")).intValue();
            }
            else{
                return ResponseDTO.error("L'ID du devoir doit être un nombre");
            }

            if (notationData.get("note") instanceof BigDecimal) {
                note = (BigDecimal) notationData.get("note");
            } else if (notationData.get("note") instanceof Number) {
                note = BigDecimal.valueOf(((Number) notationData.get("note")).doubleValue());
            } else if (notationData.get("note") instanceof String) {
                try{
                    note = new BigDecimal((String) notationData.get("note"));
                }
                catch (NumberFormatException e){
                    return ResponseDTO.error("La note n'est pas un nombre valide");

                }
            }else{
                return ResponseDTO.error("La note n'est pas au bon format");

            }
            // Validation des données
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            if (idDevoir == null) {
                return ResponseDTO.error("L'ID du devoir est requis");
            }
            if (note == null) {
                return ResponseDTO.error("La note est requise");
            }
            if (note.compareTo(BigDecimal.ZERO) < 0 || note.compareTo(new BigDecimal(20)) > 0) {
                return ResponseDTO.error("La note doit être comprise entre 0 et 20");
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
        }
        catch(Exception e){
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

    public ResponseDTO<Map<String, Object>> creerBulletinDeNoteParEtudiant(Integer idEtudiant) {
        try {

            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);
            if (notations.isEmpty()) {
                return ResponseDTO.error("Aucune note trouvée pour cet étudiant");
            }

            List<Object[]> notesParDevoir = new ArrayList<>();
            for (Notation notation : notations) {
                Devoir devoir = devoirRepository.findById(notation.getIdDevoir()).orElse(null);
                if (devoir != null) {
                    Object[] note = new Object[3];
                    note[0] = devoir.getType();
                    note[1] = notation.getNote();
                    note[2] = devoir.getCoefficient();
                    notesParDevoir.add(note);
                }
            }

            BigDecimal noteGlobale = BigDecimal.ZERO;
            BigDecimal coefficientTotal = BigDecimal.ZERO;

            for (Object[] note : notesParDevoir) {
                BigDecimal noteDevoir = (BigDecimal) note[1];
                BigDecimal coefficient = new BigDecimal(String.valueOf(note[2]));
                noteGlobale = noteGlobale.add(noteDevoir.multiply(coefficient));
                coefficientTotal = coefficientTotal.add(coefficient);

            }

            BigDecimal moyenneGlobale = coefficientTotal.compareTo(BigDecimal.ZERO) > 0 ? noteGlobale.divide(coefficientTotal, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;


            Map<String, Object> bulletin = new HashMap<>();
            bulletin.put("notesParDevoir", notesParDevoir);
            bulletin.put("moyenneGlobale", moyenneGlobale);


            return ResponseDTO.success(bulletin);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la création du bulletin de notes: " + e.getMessage());
        }
    }

    public ResponseDTO<Map<String, Object>> rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(Integer idEtudiant, Integer idDevoir) {
        try {
            Optional<Notation> notationOptional = notationRepository.findByIdEtudiantAndIdDevoir(idEtudiant, idDevoir);
            if (notationOptional.isEmpty()) {
                return ResponseDTO.error("Aucune note trouvée pour cet étudiant pour ce devoir");
            }
            BigDecimal noteGlobale = notationOptional.get().getNote();
            Map<String, Object> noteEtMoyenne = new HashMap<>();
            noteEtMoyenne.put("noteGlobale", noteGlobale);
            noteEtMoyenne.put("moyenneGlobale", noteGlobale);

            return ResponseDTO.success(noteEtMoyenne);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche de la note globale et de la moyenne globale par devoir et par étudiant: " + e.getMessage());
        }
    }

    public ResponseDTO<List<Map<String, Object>>> rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(Integer idEtudiant) {
        try {
            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);
            if (notations.isEmpty()) {
                return ResponseDTO.error("Aucune note trouvée pour cet étudiant");
            }

            Map<String, Map<String, Object>> matiereNotesMap = new HashMap<>();

            for (Notation notation : notations) {
                Devoir devoir = devoirRepository.findById(notation.getIdDevoir()).orElse(null);
                if (devoir != null) {
                    // Récupérer l'id de la matière dans l'entité Devoir
                    Integer idMatiere = devoir.getIdMatiere();
                    // Récupérer le nom de la matière
                    //TODO à remplacer quand la relation avec la matière sera faite
                    String matiereNom = "matiere n° "+idMatiere;

                    BigDecimal note = notation.getNote();
                    BigDecimal coefficient = devoir.getCoefficient();

                    if (!matiereNotesMap.containsKey(matiereNom)) {
                        Map<String, Object> matiereData = new HashMap<>();
                        matiereData.put("totalNote", BigDecimal.ZERO);
                        matiereData.put("totalCoefficient", BigDecimal.ZERO);
                        matiereNotesMap.put(matiereNom, matiereData);
                    }
                    Map<String, Object> matiereData = matiereNotesMap.get(matiereNom);
                    BigDecimal currentTotalNote = (BigDecimal) matiereData.get("totalNote");
                    BigDecimal currentTotalCoefficient = (BigDecimal) matiereData.get("totalCoefficient");

                    matiereData.put("totalNote", currentTotalNote.add(note.multiply(coefficient)));
                    matiereData.put("totalCoefficient", currentTotalCoefficient.add(coefficient));
                }
            }
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> entry : matiereNotesMap.entrySet()) {
                String matiereNom = entry.getKey();
                Map<String, Object> matiereData = entry.getValue();
                BigDecimal totalNote = (BigDecimal) matiereData.get("totalNote");
                BigDecimal totalCoefficient = (BigDecimal) matiereData.get("totalCoefficient");
                BigDecimal moyenneGlobale = totalCoefficient.compareTo(BigDecimal.ZERO) > 0 ? totalNote.divide(totalCoefficient, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
                Map<String, Object> result = new HashMap<>();
                result.put("matiere", matiereNom);
                result.put("noteGlobale", totalNote);
                result.put("moyenneGlobale", moyenneGlobale);
                resultList.add(result);
            }
            return ResponseDTO.success(resultList);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des moyennes par matière et par étudiant: " + e.getMessage());
        }
    }
}