package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Services.ClasseService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClasseRestController {

    private final ClasseService classeService;

    @Autowired
    public ClasseRestController(ClasseService classeService) {
        this.classeService = classeService;
    }

    /**
     * Récupère toutes les classes
     */
    @GetMapping("/classes")
    public ResponseDTO<List<Classe>> getAllClasses() {
        return classeService.rechercherClasses();
    }

    /**
     * Récupère une classe par son ID
     */
    @GetMapping("/classe/{id}")
    public ResponseDTO<Classe> getClasse(@PathVariable Integer id) {
        try {
            return classeService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la récupération de la classe: " + e.getMessage());
        }
    }

    /**
     * Crée ou met à jour une classe
     */
    @PostMapping("/classe/enregistrer")
    public ResponseDTO<Classe> enregistrer(@RequestBody Classe classe) {
        try {
            return classeService.enregistrer(classe);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    /**
     * Met à jour une classe existante
     */
    @PutMapping("/classe/{id}")
    public ResponseDTO<Classe> modifier(@PathVariable Integer id, @RequestBody Classe classe) {
        try {
            classe.setId(id);  // Assure que l'ID du path correspond à l'objet
            return classeService.enregistrer(classe);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la modification: " + e.getMessage());
        }
    }

    /**
     * Supprime une classe
     */
    @DeleteMapping("/classe/{id}")
    public ResponseDTO<Void> supprimer(@PathVariable Integer id) {
        try {
            return classeService.supprimer(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}