package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Services.ClasseService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classe")
@CrossOrigin(origins = "*")
public class ClasseController {

    @Autowired
    private ClasseService classeService;

    @PostMapping("/enregistrer")
    public ResponseEntity<ResponseDTO<Classe>> enregistrer(@RequestBody Classe classe) {
        ResponseDTO<Classe> response = classeService.enregistrer(classe);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<ResponseDTO<Void>> supprimer(@PathVariable("id") Integer id) {
        ResponseDTO<Void> response = classeService.supprimer(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rechercherClasses")
    public ResponseEntity<ResponseDTO<List<Classe>>> rechercherClasses() {
        ResponseDTO<List<Classe>> response = classeService.rechercherClasses();
        return ResponseEntity.ok(response);
    }
}