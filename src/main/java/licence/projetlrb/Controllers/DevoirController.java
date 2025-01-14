package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Devoir;
import licence.projetlrb.Services.DevoirService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devoir")
@CrossOrigin(origins = "*")
public class DevoirController {

    @Autowired
    private DevoirService devoirService;

    @PostMapping(
            value = "/enregistrer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<Devoir>> enregistrer(@RequestBody Devoir devoir) {
        try {
            ResponseDTO<Devoir> response = devoirService.enregistrer(devoir);
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
            ResponseDTO<Void> response = devoirService.supprimer(id);
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
                    .body(ResponseDTO.error("Une erreur est survenue lors de la suppression: " + e.getMessage()));
        }
    }

    @GetMapping(
            value = "/rechercherDevoirs",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<List<Devoir>>> rechercherDevoirs() {
        try {
            ResponseDTO<List<Devoir>> response = devoirService.rechercherDevoirs();
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
    public ResponseEntity<ResponseDTO<Devoir>> rechercherParId(@PathVariable("id") Integer id) {
        try {
            ResponseDTO<Devoir> response = devoirService.rechercherParId(id);
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