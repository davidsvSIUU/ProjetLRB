package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Matiere;
import licence.projetlrb.Services.MatiereService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MatiereController {

    private final MatiereService matiereService;

    @Autowired
    public MatiereController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    /**
     * Affiche la page de gestion des matières
     */
    @GetMapping("/gestionmatieres")
    public String gestionMatieres(Model model) {
        ResponseDTO<List<Matiere>> response = matiereService.rechercherMatieres();
        if (response.isSuccess()) {
            model.addAttribute("matieres", response.getData());
        } else {
            model.addAttribute("error", response.getMessage());
        }
        return "gestionmatieres"; // Nom de votre template Thymeleaf
    }
    /**
     * Enregistre ou met à jour une matière
     */
    @PostMapping("/matiere/enregistrer")
    public String enregistrer(@ModelAttribute Matiere matiere, Model model) {
        try {
            ResponseDTO<Matiere> response = matiereService.enregistrer(matiere);
            if (response.isSuccess()) {
                model.addAttribute("success", "Matière enregistrée avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/gestionmatieres";
    }

    /**
     * Supprime une matière
     */
    @PostMapping("/matiere/supprimer/{id}")
    public String supprimer(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Void> response = matiereService.supprimer(id);
            if (response.isSuccess()) {
                model.addAttribute("success", "Matière supprimée avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/gestionmatieres";
    }

    /**
     * Affiche le formulaire de modification d'une matière
     */
    @GetMapping("/matiere/modifier/{id}")
    public String afficherModification(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Matiere> response = matiereService.rechercherParId(id);
            if (response.isSuccess()) {
                model.addAttribute("matiere", response.getData());
            } else {
                model.addAttribute("error", response.getMessage());
                return "redirect:/gestionmatieres";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "redirect:/gestionmatieres";
        }

        return "gestionmatieres";
    }
    @GetMapping("/matiere/{id}")
    @ResponseBody
    public ResponseDTO<Matiere> getMatiere(@PathVariable Integer id) {
        try {
            return matiereService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("la récupération de la matière: " + e.getMessage());
        }
    }
}