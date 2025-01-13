package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Services.EtudiantService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiant")
@CrossOrigin(origins = "*")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @PostMapping("/enregistrer")
    public ResponseEntity<ResponseDTO<Etudiant>> enregistrer(@RequestBody Etudiant etudiant) {
        ResponseDTO<Etudiant> response = etudiantService.enregistrer(etudiant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<ResponseDTO<Void>> supprimer(@PathVariable("id") Integer id) {
        ResponseDTO<Void> response = etudiantService.supprimer(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rechercherEtudiants")
    public ResponseEntity<ResponseDTO<List<Etudiant>>> rechercherEtudiants() {
        ResponseDTO<List<Etudiant>> response = etudiantService.rechercherEtudiants();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rechercherEtudiantsDisponibles")
    public ResponseEntity<ResponseDTO<List<Etudiant>>> rechercherEtudiantsDisponibles() {
        ResponseDTO<List<Etudiant>> response = etudiantService.rechercherEtudiantsDisponibles();
        return ResponseEntity.ok(response);
    }
}