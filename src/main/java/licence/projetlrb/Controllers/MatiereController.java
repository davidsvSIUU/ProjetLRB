package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Matiere;
import licence.projetlrb.Services.MatiereService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matiere")
@CrossOrigin(origins = "*")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    @PostMapping(
            value = "/enregistrer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<Matiere>> enregistrer(@RequestBody Matiere matiere) {
        try {
            ResponseDTO<Matiere> response = matiereService.enregistrer(matiere);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error("Une erreur est survenue lors de l'enregistrement: " + e.getMessage()));
        }
    }

    @DeleteMapping(
            value = "/supprimer/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<Void>> supprimer(@PathVariable("id") Integer id) {
        try {
            ResponseDTO<Void> response = matiereService.supprimer(id);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }  catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error("Une erreur est survenue lors de la suppression: " + e.getMessage()));
        }
    }

    @GetMapping(
            value = "/rechercheMatiere",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<List<Matiere>>> rechercherMatieres() {
        try {
            ResponseDTO<List<Matiere>> response = matiereService.rechercherMatieres();
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error("Une erreur est survenue lors de la recherche: " + e.getMessage()));
        }
    }
    @GetMapping(
            value = "/rechercher/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<Matiere>> rechercherParId(@PathVariable("id") Integer id) {
        try {
            ResponseDTO<Matiere> response = matiereService.rechercherParId(id);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseDTO.error("Une erreur est survenue lors de la recherche: " + e.getMessage()));
        }
    }
}