package licence.projetlrb.Controllers;

import licence.projetlrb.DTO.ResponseDTO;
import licence.projetlrb.Entities.Etudiant;
import licence.projetlrb.Services.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/etudiant") // Mapping de base pour /api/etudiant
public class EtudiantRestController {

    private final EtudiantService etudiantService;

    @Autowired
    public EtudiantRestController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }
    /**
     * Récupère tous les étudiants
     */
    @GetMapping("/rechercherEtudiants")
    public ResponseDTO<List<Etudiant>> rechercherEtudiants() {
        return etudiantService.rechercherEtudiants();
    }

    /**
     * Enregistre ou met à jour un étudiant
     */
    @PostMapping("/enregistrer")
    public ResponseDTO<Etudiant> enregistrer(@RequestBody Map<String, Object> etudiantData) {
        try {
            Etudiant etudiant = new Etudiant();
            etudiant.setNom((String) etudiantData.get("nom"));
            etudiant.setPrenom((String) etudiantData.get("prenom"));
            etudiant.setPhoto((String) etudiantData.get("photo"));
            if(etudiantData.get("idClasse") != null){
                etudiant.setIdClasse((Integer) etudiantData.get("idClasse"));
            }
            if(etudiantData.get("id") != null){
                etudiant.setId((Integer) etudiantData.get("id"));
            }
            return etudiantService.enregistrer(etudiant);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de l'enregistrement de l'étudiant: " + e.getMessage());
        }
    }
    /**
     * Supprime un étudiant
     */
    @DeleteMapping("/supprimer/{id}")
    public ResponseDTO<Void> supprimer(@PathVariable Integer id) {
        try {
            return etudiantService.supprimer(id);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
        }
    }
    /**
     * Recherche des étudiants selon leur affectation à une classe
     */
    @GetMapping("/rechercher/disponibilite")
    public ResponseDTO<List<Etudiant>> rechercherEtudiantsDisponibles(@RequestParam boolean avecClasse) {
        try {
            List<Etudiant> etudiants = etudiantService.rechercherEtudiants().getData();
            List<Etudiant> etudiantsFiltres;

            if(avecClasse) {
                etudiantsFiltres = etudiants.stream()
                        .filter(e -> e.getIdClasse() != null)
                        .toList();
            } else {
                etudiantsFiltres = etudiants.stream()
                        .filter(e -> e.getIdClasse() == null)
                        .toList();
            }
            return ResponseDTO.success(etudiantsFiltres);
        } catch (Exception e) {
            return ResponseDTO.error("Erreur lors de la recherche des étudiants : " + e.getMessage());
        }
    }
}