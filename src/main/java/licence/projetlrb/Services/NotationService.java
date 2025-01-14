package licence.projetlrb.Services;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.Repositories.PartieDevoirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotationService {

    @Autowired
    private NotationRepository notationRepository;

    @Autowired
    private PartieDevoirRepository partieDevoirRepository;

    @Transactional
    public ResponseDTO<Notation> enregistrer(Notation notation) {
        try {
            if (notation.getIdEtudiant() == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            if (notation.getIdPartie() == null) {
                return ResponseDTO.error("L'ID de la partie du devoir est requis");
            }
            if (notation.getNote() == null) {
                return ResponseDTO.error("La note est requise");
            }

            if(notation.getNote().compareTo(BigDecimal.ZERO) < 0){
                return ResponseDTO.error("La note ne peut pas être inférieure à 0");
            }

            Notation savedNotation = notationRepository.save(notation);
            return ResponseDTO.success(savedNotation);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la notation: " + e.getMessage());
        }
    }


    @Transactional
    public ResponseDTO<Void> supprimer(Integer idNotation) {
        try {
            Notation notation = notationRepository.findById(idNotation)
                    .orElseThrow(() -> new RuntimeException("Notation non trouvée avec l'ID: " + idNotation));
            notationRepository.delete(notation);
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la notation: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<Integer, Map<String, BigDecimal>>> creerBulletinDeNoteParEtudiant(Integer idEtudiant) {
        try {
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }

            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);
            if (notations.isEmpty()) {
                return ResponseDTO.error("Aucune notation trouvée pour cet étudiant");
            }

            Map<Integer, Map<String, BigDecimal>> bulletin = new HashMap<>();
            for (Notation notation : notations) {
                Partie_Devoir partie = partieDevoirRepository.findById(notation.getIdPartie()).orElse(null);
                if (partie != null) {
                    Integer idDevoir = partie.getIdDevoir();
                    Map<String, BigDecimal> devoirNotes = bulletin.getOrDefault(idDevoir, new HashMap<>());
                    devoirNotes.put("partie_" + partie.getId(), notation.getNote());
                    bulletin.put(idDevoir, devoirNotes);
                }
            }


            return ResponseDTO.success(bulletin);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la création du bulletin de notes: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<Integer, Map<String, BigDecimal>>> rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(Integer idEtudiant) {
        try {
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);

            if (notations.isEmpty()) {
                return ResponseDTO.error("Aucune notation trouvée pour cet étudiant");
            }
            Map<Integer, Map<String, BigDecimal>> result = new HashMap<>();

            for (Notation notation : notations) {
                Partie_Devoir partie = partieDevoirRepository.findById(notation.getIdPartie()).orElse(null);
                if (partie != null) {
                    Integer idDevoir = partie.getIdDevoir();
                    BigDecimal pointsPartie = partie.getPoints();
                    BigDecimal note = notation.getNote();

                    BigDecimal notePonderee = note.multiply(pointsPartie).divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);

                    Map<String, BigDecimal> devoirInfo = result.getOrDefault(idDevoir, new HashMap<>());

                    BigDecimal totalNote = devoirInfo.getOrDefault("totalNote", BigDecimal.ZERO);
                    BigDecimal totalPoints = devoirInfo.getOrDefault("totalPoints", BigDecimal.ZERO);
                    totalNote = totalNote.add(notePonderee);
                    totalPoints = totalPoints.add(pointsPartie);
                    devoirInfo.put("totalNote", totalNote);
                    devoirInfo.put("totalPoints", totalPoints);

                    result.put(idDevoir, devoirInfo);

                }
            }
            // Calculez la moyenne globale pour chaque devoir
            for (Map.Entry<Integer, Map<String, BigDecimal>> entry : result.entrySet()) {
                Map<String, BigDecimal> devoirInfo = entry.getValue();
                BigDecimal totalNote = devoirInfo.get("totalNote");
                BigDecimal totalPoints = devoirInfo.get("totalPoints");
                if(totalPoints.compareTo(BigDecimal.ZERO) > 0){
                    BigDecimal moyenne = totalNote.divide(totalPoints.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
                    devoirInfo.put("moyenne", moyenne);

                }else{
                    devoirInfo.put("moyenne", BigDecimal.ZERO);
                }


            }


            return ResponseDTO.success(result);

        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des notes et moyennes par devoir: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<String, BigDecimal>> rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(Integer idEtudiant) {
        try {
            if (idEtudiant == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);
            if(notations.isEmpty()){
                return ResponseDTO.error("Aucune notation trouvée pour cet étudiant");
            }

            Map<String, BigDecimal> matiereAverages = new HashMap<>();
            Map<String, BigDecimal> matiereTotalPoints = new HashMap<>();
            Map<String, BigDecimal> matiereTotalNotes = new HashMap<>();
            for (Notation notation : notations) {
                Partie_Devoir partie = partieDevoirRepository.findById(notation.getIdPartie()).orElse(null);
                if (partie != null) {
                    Integer idDevoir = partie.getIdDevoir();
                    BigDecimal pointsPartie = partie.getPoints();
                    BigDecimal note = notation.getNote();
                    BigDecimal notePonderee = note.multiply(pointsPartie).divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);

                    // Récupérer la matière du devoir
                    DevoirService devoirService = new DevoirService();

                    ResponseDTO<licence.projetlrb.Entities.Devoir> devoirResponse = devoirService.rechercherParId(idDevoir);

                    if(devoirResponse.isSuccess()){
                        licence.projetlrb.Entities.Devoir devoir = devoirResponse.getData();
                        if (devoir != null) {
                            String matiereName = devoir.getIdMatiere().toString();

                            // Initialisation si la matière n'est pas encore dans le map
                            matiereTotalPoints.putIfAbsent(matiereName, BigDecimal.ZERO);
                            matiereTotalNotes.putIfAbsent(matiereName, BigDecimal.ZERO);

                            // Mettre à jour les totaux
                            matiereTotalPoints.put(matiereName, matiereTotalPoints.get(matiereName).add(pointsPartie));
                            matiereTotalNotes.put(matiereName, matiereTotalNotes.get(matiereName).add(notePonderee));
                        }

                    } else{
                        return ResponseDTO.error("erreur lors de la récupération du devoir");
                    }


                }

            }
            BigDecimal totalAllPoints = BigDecimal.ZERO;
            BigDecimal totalAllNotes = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : matiereTotalPoints.entrySet()) {
                String matiereName = entry.getKey();
                BigDecimal totalPoints = entry.getValue();
                BigDecimal totalNotes = matiereTotalNotes.get(matiereName);

                if(totalPoints.compareTo(BigDecimal.ZERO) > 0){
                    BigDecimal moyenne = totalNotes.divide(totalPoints.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
                    matiereAverages.put(matiereName, moyenne);
                }else{
                    matiereAverages.put(matiereName, BigDecimal.ZERO);
                }

                totalAllNotes = totalAllNotes.add(totalNotes);
                totalAllPoints = totalAllPoints.add(totalPoints);


            }

            if(totalAllPoints.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal moyenneGenerale = totalAllNotes.divide(totalAllPoints.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
                matiereAverages.put("moyenneGenerale", moyenneGenerale);
            }else{
                matiereAverages.put("moyenneGenerale", BigDecimal.ZERO);
            }
            return ResponseDTO.success(matiereAverages);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des moyennes par matière: " + e.getMessage());
        }
    }
}