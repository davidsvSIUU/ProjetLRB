package licence.projetlrb.Controllers;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Matiere;
import licence.projetlrb.Services.MatiereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matiere") // Mapping de base pour /api/matiere
public class MatiereRestController {

    private final MatiereService matiereService;

    @Autowired
    public MatiereRestController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    /**
     * Récupère toutes les matières
     */
    @GetMapping("/rechercherMatieres")
    public ResponseDTO<List<Matiere>> rechercherMatieres() {
        return matiereService.rechercherMatieres();
    }

    /**
     * Récupère une matière par son ID
     */
    @GetMapping("/{id}")
    public ResponseDTO<Matiere> getMatiere(@PathVariable Integer id) {
        try {
            return matiereService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la récupération de la matière: " + e.getMessage());
        }
    }


    /**
     * Enregistre ou met à jour une matière
     */
    @PostMapping("/enregistrer")
    public ResponseDTO<Matiere> enregistrer(@RequestBody Map<String, Object> matiereData) {
        try {
            Matiere matiere = new Matiere();
            matiere.setDenomination((String) matiereData.get("denomination"));
            if(matiereData.get("id") != null){
                matiere.setId((Integer) matiereData.get("id"));
            }
            return matiereService.enregistrer(matiere);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de la matière: " + e.getMessage());
        }
    }


    /**
     * Supprime une matière
     */
    @DeleteMapping("/supprimer/{id}")
    public ResponseDTO<Void> supprimer(@PathVariable Integer id) {
        try {
            return matiereService.supprimer(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de la matière: " + e.getMessage());
        }
    }
}