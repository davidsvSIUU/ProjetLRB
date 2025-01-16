package licence.projetlrb.Controllers;

import licence.projetlrb.Entities.Classe;
import licence.projetlrb.Services.ClasseService;
import licence.projetlrb.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ClasseController {

    private final ClasseService classeService;

    @Autowired
    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    /**
     * Affiche la page de gestion des classes
     */
    @GetMapping("/gestionclasses")
    public String gestionClasses(Model model) {
        ResponseDTO<List<Classe>> response = classeService.rechercherClasses();
        if (response.isSuccess()) {
            model.addAttribute("classes", response.getData());
        } else {
            model.addAttribute("error", response.getMessage());
        }
        return "gestionclasses";
    }
    /**
     * Enregistre ou met à jour une classe
     */
    @PostMapping("/classe/enregistrer")
    public String enregistrer(@ModelAttribute Classe classe, Model model) {
        try {
            ResponseDTO<Classe> response = classeService.enregistrer(classe);
            if (response.isSuccess()) {
                model.addAttribute("success", "Classe enregistrée avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/gestionclasses";
    }

    /**
     * Supprime une classe
     */
    @PostMapping("/classe/supprimer/{id}")
    public String supprimer(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Void> response = classeService.supprimer(id);
            if (response.isSuccess()) {
                model.addAttribute("success", "Classe supprimée avec succès");
            } else {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/gestionclasses";
    }

    /**
     * Affiche le formulaire de modification d'une classe
     */
    @GetMapping("/classe/modifier/{id}")
    public String afficherModification(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseDTO<Classe> response = classeService.rechercherParId(id);
            if (response.isSuccess()) {
                model.addAttribute("classe", response.getData());
            } else {
                model.addAttribute("error", response.getMessage());
                return "redirect:/gestionclasses";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "redirect:/gestionclasses";
        }

        return "gestionclasses";
    }
    @GetMapping("/classe/{id}")
    @ResponseBody
    public ResponseDTO<Classe> getClasse(@PathVariable Integer id) {
        try {
            return classeService.rechercherParId(id);
        } catch (Exception e) {
            return ResponseDTO.error("la récupération de la classe: " + e.getMessage());
        }
    }
}