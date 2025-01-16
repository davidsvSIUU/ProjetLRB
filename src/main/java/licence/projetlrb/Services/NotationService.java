package licence.projetlrb.Services;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Repositories.NotationRepository;
import licence.projetlrb.Repositories.PartieDevoirRepository;
import licence.projetlrb.Repositories.DevoirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class NotationService {

    private final NotationRepository notationRepository;
    private final PartieDevoirRepository partieDevoirRepository;
    private final DevoirRepository devoirRepository;

    @Autowired
    public NotationService(NotationRepository notationRepository,
                           PartieDevoirRepository partieDevoirRepository,
                           DevoirRepository devoirRepository) {
        this.notationRepository = notationRepository;
        this.partieDevoirRepository = partieDevoirRepository;
        this.devoirRepository = devoirRepository;
    }

    @Transactional
    public ResponseDTO<Notation> enregistrer(Notation notation) {
        try {
            // Validation des données
            if (notation.getIdEtudiant() == null) {
                return ResponseDTO.error("L'ID de l'étudiant est requis");
            }
            if (notation.getIdPartie() == null) {
                return ResponseDTO.error("L'ID de la partie du devoir est requis");
            }
            if (notation.getNote() == null) {
                return ResponseDTO.error("La note est requise");
            }
            if (notation.getNote().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseDTO.error("La note ne peut pas être négative");
            }

            // Vérification de la partie du devoir
            Optional<Partie_Devoir> partieDevoir = partieDevoirRepository.findById(notation.getIdPartie());
            if (partieDevoir.isEmpty()) {
                return ResponseDTO.error("La partie du devoir spécifiée n'existe pas");
            }

            // Vérification que la note ne dépasse pas le maximum de points
            if (notation.getNote().compareTo(partieDevoir.get().getPoints()) > 0) {
                return ResponseDTO.error("La note ne peut pas dépasser le nombre de points maximum de la partie");
            }

            // Sauvegarde de la notation
            Notation savedNotation = notationRepository.save(notation);
            return ResponseDTO.success(savedNotation);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la notation: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO<Void> supprimer(Integer idNotation) {
        try {
            if (!notationRepository.existsById(idNotation)) {
                return ResponseDTO.error("Notation non trouvée avec l'ID: " + idNotation);
            }
            notationRepository.deleteById(idNotation);
            return ResponseDTO.success(null);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la notation: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public ResponseDTO<List<Notation>> rechercherNotationsParDevoir(Integer idDevoir) {
        try {
            List<Notation> notations = new ArrayList<>();

            return ResponseDTO.success(notations);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des notations: " + e.getMessage());
        }
    }
/*
    @Transactional(readOnly = true)
    public ResponseDTO<Map<Integer, List<Notation>>> rechercherNotationsParDevoir(Integer idDevoir) {
        try {
            List<Partie_Devoir> parties = partieDevoirRepository.findByIdDevoir(idDevoir);
            Map<Integer, List<Notation>> notationsParEtudiant = new HashMap<>();

            for (Partie_Devoir partie : parties) {
                List<Notation> notations = notationRepository.findByIdPartie(partie.getId());
                for (Notation notation : notations) {
                    if (!notationsParEtudiant.containsKey(notation.getIdEtudiant())) {
                        notationsParEtudiant.put(notation.getIdEtudiant(), new ArrayList<>());
                    }
                    notationsParEtudiant.get(notation.getIdEtudiant()).add(notation);
                }
            }

            return ResponseDTO.success(notationsParEtudiant);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des notations: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<Integer, BigDecimal>> calculerMoyennesParDevoir(Integer idDevoir) {
        try {
            List<Partie_Devoir> parties = partieDevoirRepository.findByIdDevoir(idDevoir);
            Map<Integer, BigDecimal> moyennes = new HashMap<>();

            ResponseDTO<Map<Integer, List<Notation>>> notationsResponse = rechercherNotationsParDevoir(idDevoir);
            if (!notationsResponse.isSuccess()) {
                return ResponseDTO.error(notationsResponse.getMessage());
            }

            Map<Integer, List<Notation>> notationsParEtudiant = notationsResponse.getData();

            for (Map.Entry<Integer, List<Notation>> entry : notationsParEtudiant.entrySet()) {
                BigDecimal totalPoints = BigDecimal.ZERO;
                BigDecimal totalMaxPoints = BigDecimal.ZERO;

                for (Notation notation : entry.getValue()) {
                    Optional<Partie_Devoir> partie = partieDevoirRepository.findById(notation.getIdPartie());
                    if (partie.isPresent()) {
                        totalPoints = totalPoints.add(notation.getNote());
                        totalMaxPoints = totalMaxPoints.add(partie.get().getPoints());
                    }
                }

                if (totalMaxPoints.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal moyenne = totalPoints.multiply(BigDecimal.valueOf(20))
                            .divide(totalMaxPoints, 2, RoundingMode.HALF_UP);
                    moyennes.put(entry.getKey(), moyenne);
                }
            }

            return ResponseDTO.success(moyennes);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors du calcul des moyennes: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO<Map<String, BigDecimal>> calculerMoyenneParMatiere(Integer idEtudiant) {
        try {
            List<Notation> notations = notationRepository.findByIdEtudiant(idEtudiant);
            Map<String, BigDecimal> moyennesParMatiere = new HashMap<>();
            Map<String, BigDecimal> totalPointsParMatiere = new HashMap<>();
            Map<String, BigDecimal> maxPointsParMatiere = new HashMap<>();

            for (Notation notation : notations) {
                Optional<Partie_Devoir> partieOpt = partieDevoirRepository.findById(notation.getIdPartie());
                if (partieOpt.isPresent()) {
                    Partie_Devoir partie = partieOpt.get();
                    Optional<Devoir> devoirOpt = devoirRepository.findById(partie.getIdDevoir());

                    if (devoirOpt.isPresent()) {
                        String matiere = devoirOpt.get().getIdMatiere().toString();

                        totalPointsParMatiere.merge(matiere, notation.getNote(), BigDecimal::add);
                        maxPointsParMatiere.merge(matiere, partie.getPoints(), BigDecimal::add);
                    }
                }
            }

            // Calcul des moyennes par matière
            for (Map.Entry<String, BigDecimal> entry : totalPointsParMatiere.entrySet()) {
                String matiere = entry.getKey();
                BigDecimal totalPoints = entry.getValue();
                BigDecimal maxPoints = maxPointsParMatiere.get(matiere);

                if (maxPoints.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal moyenne = totalPoints.multiply(BigDecimal.valueOf(20))
                            .divide(maxPoints, 2, RoundingMode.HALF_UP);
                    moyennesParMatiere.put(matiere, moyenne);
                }
            }

            // Calcul de la moyenne générale
            BigDecimal totalPoints = totalPointsParMatiere.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalMaxPoints = maxPointsParMatiere.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalMaxPoints.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal moyenneGenerale = totalPoints.multiply(BigDecimal.valueOf(20))
                        .divide(totalMaxPoints, 2, RoundingMode.HALF_UP);
                moyennesParMatiere.put("moyenneGenerale", moyenneGenerale);
            }

            return ResponseDTO.success(moyennesParMatiere);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors du calcul des moyennes par matière: " + e.getMessage());
        }
    }*/
}