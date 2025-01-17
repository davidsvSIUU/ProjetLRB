package licence.projetlrb.Controllers;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Entities.Partie_Devoir;
import licence.projetlrb.Services.DevoirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoir") // Mapping de base pour /api/devoir
public class DevoirRestController {

    private final DevoirService devoirService;

    @Autowired
    public DevoirRestController(DevoirService devoirService) {
        this.devoirService = devoirService;
    }

    /**
     *  Récupère tous les devoirs
     */
    @GetMapping("/rechercherDevoirs")
    public ResponseDTO<List<Devoir>> rechercherDevoirs() {
        return devoirService.rechercherDevoirs();
    }

    /**
     * Enregistre ou met à jour un devoir
     */
    @PostMapping("/enregistrer")
    public ResponseDTO<Devoir> enregistrer(@RequestBody Map<String, Object> devoirData) {
        try {
            Devoir devoir = new Devoir();
            devoir.setType((String) devoirData.get("type"));

            // Convertir la date en LocalDate
            String dateString = (String) devoirData.get("dateDevoir");
            devoir.setDateDevoir(LocalDate.parse(dateString));

            // Convertir le coefficient en BigDecimal
            String coefficientString = (String) devoirData.get("coefficient");
            devoir.setCoefficient(new BigDecimal(coefficientString));

            devoir.setIdClasse((Integer) devoirData.get("idClasse"));
            devoir.setIdMatiere((Integer) devoirData.get("idMatiere"));
            List<String> pointsParPartie = (List<String>) devoirData.get("pointsParPartie");

            if (devoirData.get("id") != null) {
                devoir.setId((Integer) devoirData.get("id"));
            }
            return devoirService.enregistrer(devoir, pointsParPartie);

        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement du devoir: " + e.getMessage());
        }
    }


    /**
     * Supprime un devoir par son ID
     */
    @DeleteMapping("/supprimer/{id}")
    public ResponseDTO<Void> supprimer(@PathVariable Integer id) {
        try {
            return devoirService.supprimer(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression du devoir: " + e.getMessage());
        }
    }

    /**
     * Récupère un devoir par son ID
     */
    @GetMapping("/{id}")
    public ResponseDTO<Devoir> getDevoir(@PathVariable Integer id) {
        try {
            return devoirService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la récupération du devoir: " + e.getMessage());
        }
    }

    /**
     * Récupère les parties d'un devoir par son ID
     */
    @GetMapping("/partie/{id}")
    public ResponseDTO<List<Partie_Devoir>> getPartieDevoir(@PathVariable Integer id) {
        try {
            return devoirService.rechercherPartiesParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la récupération des parties du devoir: " + e.getMessage());
        }
    }
}