package licence.projetlrb.Controllers;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Services.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notation")
public class NotationController {

    private final NotationService notationService;

    @Autowired
    public NotationController(NotationService notationService) {
        this.notationService = notationService;
    }

    @PostMapping("/enregistrerNotation") // Changer l'URL
    public ResponseDTO<Notation> enregistrer(@RequestBody Map<String, Object> notationData) {
        return notationService.enregistrer(notationData);
    }


    @DeleteMapping("/supprimer/{id}")
    public ResponseDTO<Void> supprimer(@PathVariable Integer id) {
        return notationService.supprimer(id);
    }


    @GetMapping("/creerBulletinDeNoteParEtudiant/{idEtudiant}")
    public ResponseDTO<Map<String,Object>> creerBulletinDeNoteParEtudiant(@PathVariable Integer idEtudiant) {
        return notationService.creerBulletinDeNoteParEtudiant(idEtudiant);
    }


    @GetMapping("/rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant/{idEtudiant}/{idDevoir}")
    public ResponseDTO<Map<String,Object>> rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(@PathVariable Integer idEtudiant, @PathVariable Integer idDevoir) {
        return notationService.rechercherNoteGlobalEtMoyenneGlobaleParDevoirParEtudiant(idEtudiant, idDevoir);
    }

    @GetMapping("/rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant/{idEtudiant}")
    public ResponseDTO<List<Map<String, Object>>> rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(@PathVariable Integer idEtudiant) {
        return notationService.rechercherMoyenneEtMoyenneGlobalParMatiereParEtudiant(idEtudiant);
    }
    @GetMapping("/rechercherNotationsParDevoir/{idDevoir}")
    public ResponseDTO<List<Notation>> rechercherNotationsParDevoir(@PathVariable Integer idDevoir) {
        return notationService.rechercherNotationsParDevoir(idDevoir);
    }
}