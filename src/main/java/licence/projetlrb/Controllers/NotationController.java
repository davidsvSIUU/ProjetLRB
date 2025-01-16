package licence.projetlrb.Controllers;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Notation;
import licence.projetlrb.Services.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notation")
@CrossOrigin(origins = "*")
public class NotationController {

    @Autowired
    private NotationService notationService;

    @PostMapping(
            value = "/enregistrer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO<Notation>> enregistrer(@RequestBody Map<String, Object> notationData) {
        try {
            ResponseDTO<Notation> response = notationService.enregistrer(notationData);
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
            ResponseDTO<Void> response = notationService.supprimer(id);
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
}